package io.plakhov.wiki.dictionary.parser.dto

data class DeclensionResponseDto(
    val morphologicalDescription: String,
    val typeOfDeclension: String,
    val Declensions: List<DeclensionDto>
) {
}