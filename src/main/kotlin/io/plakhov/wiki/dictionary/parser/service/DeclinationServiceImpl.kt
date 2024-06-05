package io.plakhov.wiki.dictionary.parser.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto
import io.plakhov.wiki.dictionary.parser.dto.DeclinationResponseDto
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.regex.Pattern

@Service
class DeclinationServiceImpl(
    private val httpClient: HttpClient
) : DeclinationService {

    companion object {
        const val DECLINATION_TABLE_CSS_QUERY = "table.morfotable.ru"
        const val DECLINATION_DEFINITION_CSS_QUERY = "p:contains(Зализняк)"
        const val REGEX_BRACKET = "\\((.*?)\\)"
        const val REGEX_DIGIT = ".*\\d.*"
    }

    val REGEX_PATTERN_BRACKET = Pattern.compile(REGEX_BRACKET)

    @Value("\${wiki.dictionary.baseUri}")
    private lateinit var baseUri: String

    override suspend fun findFirstDeclinations(word: String): DeclinationResponseDto {
        return findDeclinations(word).first()
    }

    override suspend fun findAllDeclinations(word: String): List<DeclinationResponseDto> {
        return findDeclinations(word)
    }

    private suspend fun findDeclinations(word: String): List<DeclinationResponseDto> {
        val httpResponse = httpClient.get(baseUri + word).bodyAsText()
        val parsedResponse = Jsoup
            .parse(httpResponse)
        val result = parsedResponse.select(DECLINATION_TABLE_CSS_QUERY).map { element ->
            element.childNode(BigDecimal.ONE.toInt())
                .childNodes()
                .asSequence()
                .filterIsInstance<Element>()
                .drop(BigDecimal.ONE.toInt())
                .map {
                    it.childNodes().filterIsInstance<Element>()
                }
                .filter {
                    it.size == 3
                }
                .map {
                    DeclinationDto(
                        it[0].childNodes().filterIsInstance<Element>().first().attribute("title").value.trim(),
                        it[1].childNodes().filterIsInstance<TextNode>().first().text().trim(),
                        it[2].childNodes().filterIsInstance<TextNode>().first().text().trim()
                    )
                }
                .toList()
        }.filter { it.size == 6 }
        val definitionDeclination = parsedResponse.select(DECLINATION_DEFINITION_CSS_QUERY)
        return result.mapIndexed { index, declinationDtos ->
            val definition = definitionDeclination[index]
            var typeOfDeclination = ""
            val matcher = REGEX_PATTERN_BRACKET.matcher(definition.text())
            if (matcher.find()) {
                typeOfDeclination = matcher.group().split(" ").find { it.matches(REGEX_DIGIT.toRegex()) } ?: ""
            }
            DeclinationResponseDto(definition.text(), typeOfDeclination, declinationDtos)
        }
    }
}