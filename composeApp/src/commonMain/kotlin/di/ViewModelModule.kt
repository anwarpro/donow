package di

import AppViewModel
import org.koin.dsl.module
import ui.screen.LoginViewModel

val viewModelModule = module {
    factory {
        AppViewModel(supabaseClient = get(), taskApi = get())
    }
    factory {
        LoginViewModel(supabaseClient = get())
    }
}