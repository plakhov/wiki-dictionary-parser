package io.plakhov.wiki.dictionary.parser.service

enum class DescriptionType(val cssQuery: String) {
    ZALIZNYAK("p:contains(Зализняк)"),
    SCHEMA("p:contains(склонения по схеме)")


}