package di

import io.github.agrevster.pocketbaseKotlin.PocketbaseClient
import io.ktor.http.URLProtocol
import org.koin.dsl.module

val pocketBaseModule = module {
    single<PocketbaseClient> {
        PocketbaseClient(
            {
                protocol = URLProtocol.HTTP
                host = "localhost"
                port = 8090
            }
        )
    }
}