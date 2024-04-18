package io.plakhov.wiki.dictionary.parser.controller

import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto
import io.plakhov.wiki.dictionary.parser.service.DeclinationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController("/declinations")
class DeclinationController(
    private val declinationService: DeclinationService
) {

    @GetMapping("/{word}")
    suspend fun findDeclinations(@PathVariable word: String) : DeclinationDto {
        return declinationService.findDeclinations(word)
    }
}