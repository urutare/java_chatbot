module com.urutare.javachat {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    requires langchain4j.core;
    // Logging
    requires org.apache.logging.log4j;
    requires langchain4j.embeddings.all.minilm.l6.v2.q;
    requires  langchain4j.embeddings;
    requires langchain4j.open.ai;
    requires langchain4j;
    opens com.urutare.javachat to javafx.fxml;
    exports com.urutare.javachat;
}
