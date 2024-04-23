package di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.AuthConfig
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import org.koin.dsl.module

expect fun AuthConfig.platformGoTrueConfig()

val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://ctbvplmwlpqfscnsbfxw.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN0YnZwbG13bHBxZnNjbnNiZnh3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTM4NTUwOTUsImV4cCI6MjAyOTQzMTA5NX0.iojl06NfgkMzaj-a__4rndyfysInD9kdH-ejAHDvog0"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single<RealtimeChannel> {
        get<SupabaseClient>().channel("tasks")
    }
}