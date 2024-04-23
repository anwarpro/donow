package data.repository.task

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("created_at")
    val createdAt: Instant
)


sealed interface TaskApi {

    suspend fun retrieveTasks(): List<Task>

    suspend fun createTask(title: String, description: String): Task

    suspend fun deleteTask(id: Int)

}

internal class TaskApiImpl(
    private val client: SupabaseClient
) : TaskApi {
    private val table = client.postgrest["tasks"]

    override suspend fun retrieveTasks(): List<Task> = table.select().decodeList()

    override suspend fun createTask(title: String, description: String): Task {
        val user = (client.auth.currentSessionOrNull() ?: error("No session available")).user
            ?: error("No user available")

        return table.insert(buildJsonObject {
            put("title", title)
            put("description", description)
            put("user_id", user.id)
        }) {
            select()
        }.decodeSingle()
    }

    override suspend fun deleteTask(id: Int) {
        table.delete {
            filter {
                Task::id eq id
            }
        }
    }

}