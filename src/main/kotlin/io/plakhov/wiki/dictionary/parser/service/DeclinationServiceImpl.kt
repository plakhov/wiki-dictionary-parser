package io.plakhov.wiki.dictionary.parser.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DeclinationServiceImpl(
    private val httpClient: HttpClient
) : DeclinationService {

    @Value("\${wiki.dictionary.baseUri}")
    private lateinit var baseUri: String

    override suspend fun findDeclinations(word: String): DeclinationDto {
        val response = httpClient.get("$baseUri/$word")
        return DeclinationDto(listOf())
    }
}