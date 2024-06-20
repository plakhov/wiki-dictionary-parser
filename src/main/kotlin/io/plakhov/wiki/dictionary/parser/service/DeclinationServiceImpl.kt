package io.plakhov.wiki.dictionary.parser.service

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.plakhov.wiki.dictionary.parser.dto.DeclensionDto
import io.plakhov.wiki.dictionary.parser.dto.DeclensionResponseDto
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.regex.Pattern

/**
 * Сервис для работы со склонением слов.
 */
@Service
class DeclensionServiceImpl(
    private val httpClient: HttpClient
) : DeclensionService {

    /**
     * CSS-селектор для таблицы морфологической таблицы.
     */
    companion object {
        const val Declension_TABLE_CSS_QUERY = "table.morfotable.ru"
        const val REGEX_BRACKET = "\\((.*)\\)"
        const val REGEX_DIGIT = ".*\\d.*"
    }

    val regexPatternBracket = Pattern.compile(REGEX_BRACKET)!!

    @Value("\${wiki.dictionary.baseUri}")
    private lateinit var baseUri: String

    /**
     * Поиск первого склонения слова.
     *
     * @param word слово
     * @return первое склонение слова
     */
    override suspend fun findFirstDeclensions(word: String): DeclensionResponseDto {
        return findDeclensions(word).first()
    }

    /**
     * Поиск всех склонений слова.
     *
     * @param word слово
     * @return все склонения слова
     */
    override suspend fun findAllDeclensions(word: String): List<DeclensionResponseDto> {
        return findDeclensions(word)
    }

    /**
     * Поиск склонений слова.
     *
     * @param word слово
     * @return склонения слова
     */
    private suspend fun findDeclensions(word: String): List<DeclensionResponseDto> {
        val httpResponse = httpClient.get(baseUri + word).bodyAsText()
        val parsedResponse = Jsoup
            .parse(httpResponse)
        val morfoTables = findMorfoTables(parsedResponse)
        val definitionDeclensions = findDefinitionWord(parsedResponse)
        return morfoTableToDeclensionDto(morfoTables).mapIndexed { index, DeclensionDtos ->
            val definition = definitionDeclensions[index]
            DeclensionResponseDto(definition.first, definition.second, DeclensionDtos)
        }
    }

    /**
     * Поиск таблиц морфологической таблицы.
     *
     * @param parsedResponse отпарсенный ответ
     * @return таблицы морфологической таблицы
     */
    private suspend fun findMorfoTables(parsedResponse: Document): List<List<List<Element>>> {
        return parsedResponse.select(Declension_TABLE_CSS_QUERY)
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

    /**
     * Преобразование таблиц морфологической таблицы в склонения.
     *
     * @param morfoTables таблицы морфологической таблицы
     * @return склонения
     */
    private fun morfoTableToDeclensionDto(morfoTables: List<List<List<Element>>>): List<List<DeclensionDto>> {
        return morfoTables.map { table ->
            table.map {
                DeclensionDto(
                    it[0].childNodes().filterIsInstance<Element>().first().attribute("title").value.trim(),
                    it[1].childNodes().filterIsInstance<TextNode>().first().text().trim(),
                    it[2].childNodes().filterIsInstance<TextNode>().first().text().trim()
                )
            }
        }
    }

    /**
     * Поиск определения слова.
     *
     * @param parsedResponse отпарсенный ответ
     * @return определения слова
     */
    private suspend fun findDefinitionWord(parsedResponse: Document): List<Pair<String, String>> {
        return DescriptionType.entries.flatMap { typeOfDescription ->
            parsedResponse.select(typeOfDescription.cssQuery).asIterable().toList().map {
                it to parseTypeOfDeclensions(typeOfDescription, it)
            }
        }.sortedBy { it.first.siblingIndex() }.map { Pair(it.second, it.first.text()) }
    }


    /**
     * Парсинг типа склонения.
     *
     * @param descriptionType тип описания
     * @param description описание
     * @return тип склонения
     */
    private fun parseTypeOfDeclensions(descriptionType: DescriptionType, description: Element): String {
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

