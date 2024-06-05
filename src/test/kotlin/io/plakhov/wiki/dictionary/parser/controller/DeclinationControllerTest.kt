package io.plakhov.wiki.dictionary.parser.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.post


@SpringBootTest
class DeclinationControllerTest : AbstractIntegrationTest() {

    @Test
    fun when_we_test_word_with_one_declinations() {
        val result = mockMvc.post("/declination/слово")
            .andExpect { status { isOk() } }
            .andReturn()

//            .andReturn()
//            .parseResponse(mapper, jacksonTypeRef<List<List<DeclinationDto>>>())
//        assertThat(result).hasSize(1)
    }
}