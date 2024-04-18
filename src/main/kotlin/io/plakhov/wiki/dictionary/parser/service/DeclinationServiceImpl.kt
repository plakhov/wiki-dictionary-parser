package io.plakhov.wiki.dictionary.parser.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class DeclinationServiceImpl(
        private val httpClient: HttpClient
) : DeclinationService {

    @Value("\${wiki.dictionary.baseUri}")
    private lateinit var baseUri: String

    override suspend fun findDeclinations(word: String): List<DeclinationDto> {
        return Jsoup
                .parse(httpClient.get(baseUri + word).bodyAsText())
                .select("table.morfotable.ru")
                .first()
                .childNode(1)
                .childNodes()
                .filterIsInstance<Element>()
                .drop(1)
                .map {
                    val row = it.childNodes().filterIsInstance<Element>()
                    println(row)
                    DeclinationDto(
                            row[0].childNodes().filterIsInstance<Element>().first().attribute("title").value.trim(),
                            row[1].childNodes().filterIsInstance<TextNode>().first().text().trim(),
                            row[2].childNodes().filterIsInstance<TextNode>().first().text().trim()
                    )
                }
    }
}