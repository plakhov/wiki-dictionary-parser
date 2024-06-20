package io.plakhov.wiki.dictionary.parser.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto
import io.plakhov.wiki.dictionary.parser.dto.DeclinationResponseDto
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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
        const val REGEX_BRACKET = "\\((.*)\\)"
        const val REGEX_DIGIT = ".*\\d.*"
    }

    val regexPatternBracket = Pattern.compile(REGEX_BRACKET)!!

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
        val morfoTables = findMorfoTables(parsedResponse)
        val definitionDeclinations = findDefinitionDeclination(parsedResponse)
        return morfoTableToDeclinationDto(morfoTables).mapIndexed { index, declinationDtos ->
            val definition = definitionDeclinations[index]
            DeclinationResponseDto(definition.first, definition.second, declinationDtos)
        }
    }

    private suspend fun findMorfoTables(parsedResponse: Document): List<List<List<Element>>> {
        return parsedResponse.select(DECLINATION_TABLE_CSS_QUERY)
            .sortedBy { it.siblingIndex() }
            .map { element ->
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
                    }.toList()
            }.filter { it.size >= 6 }
    }

    private fun morfoTableToDeclinationDto(morfoTables: List<List<List<Element>>>): List<List<DeclinationDto>> {
        return morfoTables.map { table ->
            table.map {
                DeclinationDto(
                    it[0].childNodes().filterIsInstance<Element>().first().attribute("title").value.trim(),
                    it[1].childNodes().filterIsInstance<TextNode>().first().text().trim(),
                    it[2].childNodes().filterIsInstance<TextNode>().first().text().trim()
                )
            }
        }
    }

    //first - morphologicalDescription
    //second - typeOfDeclination
    private suspend fun findDefinitionDeclination(parsedResponse: Document): List<Pair<String, String>> {
        return DescriptionType.entries.flatMap { typeOfDescription ->
            parsedResponse.select(typeOfDescription.cssQuery).asIterable().toList().map {
                it to parseTypeOfDeclinations(typeOfDescription, it)
            }
        }.sortedBy { it.first.siblingIndex() }.map { Pair(it.second, it.first.text()) }
    }

    private fun parseTypeOfDeclinations(descriptionType: DescriptionType, description: Element): String {
        return when (descriptionType) {
            DescriptionType.ZALIZNYAK -> {
                val matcher = regexPatternBracket.matcher(description.text())
                if (matcher.find()) {
                    matcher.group().split(" ").find { it.matches(REGEX_DIGIT.toRegex()) } ?: ""
                } else {
                    ""
                }
            }

            DescriptionType.SCHEMA -> description.text().split(" ").find { it.matches(REGEX_DIGIT.toRegex()) }
                ?.replace(":", "") ?: ""
        }
    }
}