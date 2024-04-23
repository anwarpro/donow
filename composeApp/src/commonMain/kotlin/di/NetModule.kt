package di

import data.repository.task.TaskApi
import data.repository.task.TaskApiImpl
import org.koin.dsl.module

val netModule = module {
    single<TaskApi> { TaskApiImpl(client = get()) }
}