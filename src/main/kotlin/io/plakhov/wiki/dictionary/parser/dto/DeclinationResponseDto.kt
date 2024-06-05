package io.plakhov.wiki.dictionary.parser.dto

data class DeclinationResponseDto(
    val morphologicalDescription: String,
    val typeOfDeclination: String,
    val declinations: List<DeclinationDto>
) {
}