package di

import AppViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory {
        AppViewModel(supabaseClient = get(), taskApi = get())
    }
}