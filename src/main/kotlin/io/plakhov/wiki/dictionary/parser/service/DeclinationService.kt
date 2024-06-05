package io.plakhov.wiki.dictionary.parser.service

import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto
import io.plakhov.wiki.dictionary.parser.dto.DeclinationResponseDto

interface DeclinationService {
    suspend fun findFirstDeclinations(word: String) : DeclinationResponseDto
    suspend fun findAllDeclinations(word: String) : List<DeclinationResponseDto>
}
