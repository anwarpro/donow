package di

import AppViewModel
import org.koin.dsl.module
import ui.screen.home.HomeViewModel
import ui.screen.login.LoginViewModel

val viewModelModule = module {
    factory {
        AppViewModel(supabaseClient = get(), taskApi = get(), pocketbaseClient = get())
    }
    factory {
        LoginViewModel(supabaseClient = get(), pocketbaseClient = get())
    }
    factory {
        HomeViewModel(taskApi = get(), supabaseClient = get())
    }
}