package di

import io.github.jan.supabase.gotrue.AuthConfig

actual fun AuthConfig.platformGoTrueConfig() {
    scheme = "io.jan.supabase"
    host = "login"
}