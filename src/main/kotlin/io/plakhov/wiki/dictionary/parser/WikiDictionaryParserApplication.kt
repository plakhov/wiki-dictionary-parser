package io.plakhov.wiki.dictionary.parser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WikiDictionaryParserApplication

fun main(args: Array<String>) {
    runApplication<WikiDictionaryParserApplication>(*args)
}
