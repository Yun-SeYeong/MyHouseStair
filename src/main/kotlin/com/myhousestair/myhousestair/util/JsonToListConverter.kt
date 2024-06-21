package com.myhousestair.myhousestair.util


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter

class JsonToListConverter : AttributeConverter<List<String>, String> {
    private val objectMapper = ObjectMapper()

    override fun convertToEntityAttribute(p0: String?): List<String> {
        return try {
            objectMapper.readValue(
                p0!!,
                object : TypeReference<List<String>>() {}
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun convertToDatabaseColumn(p0: List<String>?): String {
        return try {
            objectMapper.writeValueAsString(p0)
        } catch (e: Exception) {
            "[]"
        }
    }
}