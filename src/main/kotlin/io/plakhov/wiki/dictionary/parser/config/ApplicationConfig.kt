package io.plakhov.wiki.dictionary.parser.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
        }
    }
}