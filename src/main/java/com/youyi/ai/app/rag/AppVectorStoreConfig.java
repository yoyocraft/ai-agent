package com.youyi.ai.app.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/03
 */
@Configuration
@RequiredArgsConstructor
public class AppVectorStoreConfig {

    private final AppDocumentLoader appDocumentLoader;

    @Bean
    public VectorStore appVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
            .build();
        // vectorStore.add(appDocumentLoader.loadMarkdown());
        return vectorStore;
    }

    @Bean
    public VectorStore romanticPartnerVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
            .build();
        // vectorStore.add(appDocumentLoader.loadJson());
        return vectorStore;
    }
}
