package io.plakhov.wiki.dictionary.parser.controller

import io.klogging.Klogging
import io.plakhov.wiki.dictionary.parser.dto.DeclensionResponseDto
import io.plakhov.wiki.dictionary.parser.service.DeclensionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/Declension")
class DeclensionController(
    private val DeclensionService: DeclensionService
): Klogging {

    @PostMapping("/{word}")
    suspend fun findDeclensions(@PathVariable word: String): List<DeclensionResponseDto> {
        try {
            return DeclensionService.findAllDeclensions(word)
        } catch (e: Throwable){
            logger.error(e.message!!, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)
        }
    }
}