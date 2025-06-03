package com.youyi.ai.app.advisor;

import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/02
 */
public class LoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(LoggerAdvisor.class);

    @Nonnull
    @Override
    public AdvisedResponse aroundCall(@Nonnull AdvisedRequest advisedRequest, @Nonnull CallAroundAdvisorChain chain) {
        advisedRequest = before(advisedRequest);

        AdvisedResponse response = chain.nextAroundCall(advisedRequest);

        observeAfter(response);

        return response;
    }

    @Nonnull
    @Override
    public Flux<AdvisedResponse> aroundStream(@Nonnull AdvisedRequest advisedRequest, @Nonnull StreamAroundAdvisorChain chain) {
        advisedRequest = before(advisedRequest);

        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);

        return new MessageAggregator().aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        logger.info("request: {}", request.userText());
        return request;
    }

    private void observeAfter(AdvisedResponse response) {
        AssistantMessage output = response.response().getResult().getOutput();
        logger.info("response: {}", output.getText());
    }
}
