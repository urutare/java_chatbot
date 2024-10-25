package com.urutare.javachat.customTools

import com.urutare.javachat.ApiKeys
import com.urutare.javachat.search.TavilyWebSearchEngine
import dev.langchain4j.web.search.WebSearchOrganicResult
import java.time.Duration

class SearchTool:Search {
    companion object {
        private var tavilyApiKey: String? = ApiKeys.TAVILY_API_KEY
    }

    override fun searchWithRawContent() {
        // Given
        val tavilyWebSearchEngine = TavilyWebSearchEngine(
            "https://api.tavily.com/",
            tavilyApiKey,
            Duration.ofSeconds(50),
            "1",
            false,
            true,
            listOf("example.com"),
            listOf("example.org")
        )

        val webSearchResults = tavilyWebSearchEngine.search("What is LangChain4j?")

        // then
        val results = webSearchResults.results()

        println(results)
    }

    override fun searchWithAnswer(query: String): MutableList<WebSearchOrganicResult>? {
        val tavilyWebSearchEngine = TavilyWebSearchEngine(
            "https://api.tavily.com/",
            tavilyApiKey,
            Duration.ofSeconds(50),
            "1",
            true,
            false,
            listOf("example.com"),
            listOf("example.org")
        )
        val webSearchResults = tavilyWebSearchEngine.search(query)


        val results = webSearchResults.results()

        return results
    }

    override fun getTavilyApiKey(): String {
        return tavilyApiKey!!
    }


}