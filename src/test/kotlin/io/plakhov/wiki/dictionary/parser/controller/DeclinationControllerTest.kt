package io.plakhov.wiki.dictionary.parser.controller

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.plakhov.wiki.dictionary.parser.dto.DeclinationDto
import io.plakhov.wiki.dictionary.parser.parseResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.get


@SpringBootTest
class DeclinationControllerTest : AbstractIntegrationTest() {

    @Test
    fun when_we_test_word_with_one_declinations() {
        val result = mockMvc.get("/declinations/слово")
            .andExpect { status { isOk() } }
            .andReturn()

//            .andReturn()
//            .parseResponse(mapper, jacksonTypeRef<List<List<DeclinationDto>>>())
//        assertThat(result).hasSize(1)
    }
}