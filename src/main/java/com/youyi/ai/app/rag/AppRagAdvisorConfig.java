package com.youyi.ai.app.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/03
 */
@Configuration
public class AppRagAdvisorConfig {

    @Bean
    public Advisor appDashscopeRagAdvisor() {
        var dashScopeApi = new DashScopeApi(System.getenv("DASHSCOPE_API_KEY"));
        DocumentRetriever retriever = new DashScopeDocumentRetriever(
            dashScopeApi,
            DashScopeDocumentRetrieverOptions.builder()
                .withIndexName("LoveApp")
                .build()
        );

        return RetrievalAugmentationAdvisor.builder()
            .documentRetriever(retriever)
            .build();
    }
}
