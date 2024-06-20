package io.plakhov.wiki.dictionary.parser.controller;

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class DeclensionControllerTest : SpringTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun findDeclinations() {
        mockMvc.perform(post("/declination/{0}", "слово"))
            .andExpect(status().isOk())
            .andDo(print())
    }


}
