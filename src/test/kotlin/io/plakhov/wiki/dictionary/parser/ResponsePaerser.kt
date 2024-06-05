package io.plakhov.wiki.dictionary.parser

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MvcResult
import java.nio.charset.StandardCharsets.UTF_8

fun <T> MvcResult.parseResponse(mapper: ObjectMapper, modelType: Class<T>?): T {
    val content = this.response.getContentAsString(UTF_8)
    return mapper.readValue(content, modelType)
}

fun <T> MvcResult.parseResponse(
    mapper: ObjectMapper,
    typeReference: TypeReference<T>?
): T {
    val content = this.response.getContentAsString(UTF_8)
    return mapper.readValue(content, typeReference)
}