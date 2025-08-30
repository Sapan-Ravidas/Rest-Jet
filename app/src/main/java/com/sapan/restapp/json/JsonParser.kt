package com.sapan.restapp.json

import org.json.JSONArray
import org.json.JSONObject

object JsonParser {

    fun parseJson(jsonString: String): List<JsonNode> {
        return try {
            when {
                jsonString.trim().startsWith('{') -> {
                    val jsonObject = JSONObject(jsonString)
                    parseObject("", jsonObject, 0) // Empty key for root object
                }
                jsonString.trim().startsWith('[') -> {
                    val jsonArray = JSONArray(jsonString)
                    parseArray("", jsonArray, 0) // Empty key for root array
                }
                else -> listOf(JsonNode("", jsonString, 0))
            }
        } catch (e: Exception) {
            listOf(JsonNode("", jsonString, 0))
        }
    }

    /**
     *
     */
    private fun parseObject(key: String, jsonObject: JSONObject, level: Int): List<JsonNode> {
        val nodes = mutableListOf<JsonNode>()

        jsonObject.keys().forEach { childKey ->
            val value = jsonObject[childKey]
            when (value) {
                is JSONObject -> {
                    nodes.add(JsonNode(
                        key = childKey,
                        value = null,
                        level = level,
                        isExpanded = false,
                        isObject = true,
                        isArray = false,
                        children = parseObject("", value, level + 1)
                    ))
                }
                is JSONArray -> {
                    nodes.add(JsonNode(
                        key = childKey,
                        value = null,
                        level = level,
                        isExpanded = false,
                        isObject = false,
                        isArray = true,
                        children = parseArray("", value, level + 1)
                    ))
                }
                else -> {
                    nodes.add(JsonNode(childKey, value, level))
                }
            }
        }

        return nodes
    }

    /**
     *
     */
    private fun parseArray(key: String, jsonArray: JSONArray, level: Int): List<JsonNode> {
        val nodes = mutableListOf<JsonNode>()

        for (i in 0 until jsonArray.length()) {
            val value = jsonArray[i]
            when (value) {
                is JSONObject -> {
                    nodes.add(JsonNode(
                        key = "[$i]",
                        value = null,
                        level = level,
                        isExpanded = false,
                        isObject = true,
                        isArray = false,
                        children = parseObject("", value, level + 1)
                    ))
                }
                is JSONArray -> {
                    nodes.add(JsonNode(
                        key = "[$i]",
                        value = null,
                        level = level,
                        isExpanded = false,
                        isObject = false,
                        isArray = true,
                        children = parseArray("", value, level + 1)
                    ))
                }
                else -> {
                    nodes.add(JsonNode("[$i]", value, level))
                }
            }
        }

        return nodes

        return nodes
    }

    /**
     *
     */
    fun toggleNode(nodes: List<JsonNode>, target: JsonNode): List<JsonNode> {
        return nodes.map { node ->
            if (node == target) {
                node.copy(isExpanded = !node.isExpanded)
            } else if (node.children.isNotEmpty()) {
                node.copy(children = toggleNode(node.children, target))
            } else {
                node
            }
        }
    }

    /**
     *
     */
    fun flattenNodes(nodes: List<JsonNode>): List<JsonNode> {
        val flattened = mutableListOf<JsonNode>()
        nodes.forEach { node ->
            flattened.add(node)
            if (node.isExpanded && node.children.isNotEmpty()) {
                flattened.addAll(flattenNodes(node.children))
            }
        }
        return flattened
    }
}