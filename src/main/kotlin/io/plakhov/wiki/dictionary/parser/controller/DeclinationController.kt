package io.plakhov.wiki.dictionary.parser.controller

import io.klogging.Klogging
import io.plakhov.wiki.dictionary.parser.dto.DeclinationResponseDto
import io.plakhov.wiki.dictionary.parser.service.DeclinationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/declination")
class DeclinationController(
    private val declinationService: DeclinationService
): Klogging {

    @PostMapping("/{word}")
    suspend fun findDeclinations(@PathVariable word: String) : List<DeclinationResponseDto> {
        try {
            return declinationService.findAllDeclinations(word)
        } catch (e: Throwable){
            logger.error(e.message!!, e)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message, e)
        }
    }
}