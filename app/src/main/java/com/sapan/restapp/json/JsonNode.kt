package com.sapan.restapp.json

data class JsonNode(
    val key: String,
    val value: Any?,
    val level: Int = 0,
    val isExpanded: Boolean = false,
    val isObject: Boolean = false,
    val isArray: Boolean = false,
    val children: List<JsonNode> = emptyList()
)
