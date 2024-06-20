package io.plakhov.wiki.dictionary.parser.service

import io.plakhov.wiki.dictionary.parser.dto.DeclensionResponseDto

interface DeclensionService {
    suspend fun findFirstDeclensions(word: String): DeclensionResponseDto
    suspend fun findAllDeclensions(word: String): List<DeclensionResponseDto>
}
