package io.plakhov.wiki.dictionary.parser.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractIntegrationTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var mapper: ObjectMapper
}