package com.youyi.ai.core;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.youyi.ai.BaseIT;
import com.youyi.ai.advisor.LoggerAdvisor;
import com.youyi.ai.util.GsonUtil;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/02
 */
class SpringAiIT extends BaseIT {

    private static final Logger logger = LoggerFactory.getLogger(SpringAiIT.class);

    @Resource ChatModel dashscopeChatModel;

    @Test
    void testChatWithImage() throws IOException {
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
            .defaultAdvisors(
                new LoggerAdvisor()
            )
            .build();

        List<Media> mediaList = List.of(
            new Media(
                MimeTypeUtils.IMAGE_JPEG,
                new ClassPathResource("1.jpeg").getURL()
            )
        );
        ChatResponse response = chatClient
            .prompt(new Prompt(
                new UserMessage("解释这张图片", mediaList),
                DashScopeChatOptions.builder().withModel("qwen-vl-max").build()
            ))
            .call()
            .chatResponse();
        logger.info("response: {}", GsonUtil.toJson(response));
    }
}
