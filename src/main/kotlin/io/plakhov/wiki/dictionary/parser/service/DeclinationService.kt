package io.plakhov.wiki.dictionary.parser.service

import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto

interface DeclinationService {
    suspend fun findDeclinations(word: String) : List<DeclinationDto>
}
