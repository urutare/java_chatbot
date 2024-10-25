package com.urutare.javachat.customTools

import dev.langchain4j.web.search.WebSearchOrganicResult

interface Search {
    fun searchWithRawContent()
    fun searchWithAnswer(query: String): MutableList<WebSearchOrganicResult>?
    fun getTavilyApiKey(): String
}