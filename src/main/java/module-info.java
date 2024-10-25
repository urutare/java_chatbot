module com.urutare.javachat {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires langchain4j.core;
    // Logging
    requires org.apache.logging.log4j;
    requires langchain4j.embeddings.all.minilm.l6.v2.q;
    requires langchain4j.embeddings;
    requires langchain4j.open.ai;
    requires langchain4j;
    requires org.checkerframework.checker.qual;
    requires retrofit2;
    requires okhttp3;
    requires okhttp3.sse;
    requires static lombok;
    requires retrofit2.converter.jackson;
    requires kotlin.stdlib;
    opens com.urutare.javachat to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.urutare.javachat.search to com.fasterxml.jackson.databind;
    exports com.urutare.javachat;
    exports com.urutare.javachat.customTools;

}