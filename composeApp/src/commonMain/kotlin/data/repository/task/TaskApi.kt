package data.repository.task

import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.github.agrevster.pocketbaseKotlin.models.Record
import io.github.agrevster.pocketbaseKotlin.models.utils.ListResult
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TaskRecord(
    val title: String,
    val description: String,
    val author: String? = null,
    val createdAt: Instant? = null
) : Record()

sealed interface TaskApi {

    suspend fun retrieveTasks(): ListResult<TaskRecord>

    suspend fun createTask(title: String, description: String): TaskRecord

    suspend fun deleteTask(id: String): Boolean

}

internal class TaskApiImpl(
    private val client: PocketbaseClient
) : TaskApi {
    override suspend fun retrieveTasks(): ListResult<TaskRecord> =
        client.records.getList(sub = "tasks", page = 1, perPage = 20)

    override suspend fun createTask(title: String, description: String): TaskRecord {
        val response = client.records.create<TaskRecord>(
            sub = "tasks",
            body = Json.encodeToString(
                TaskRecord(
                    title = title,
                    description = description
                )
            )
        )

        return response
    }

    override suspend fun deleteTask(id: String): Boolean {
        return client.records.delete(sub = "tasks", id = id)
    }
}