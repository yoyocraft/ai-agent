package com.youyi.ai.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;

/**
 * spring.ai.ollama.base-url=http://localhost:11434
 * spring.ai.ollama.chat.model=qwen3:0.6b
 *
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/01
 */

// @Component
public class OllamaInvoke implements CommandLineRunner {

    @Resource
    private ChatModel ollamaChatModel;

    @Override
    public void run(String... args) throws Exception {
        ChatResponse response = ollamaChatModel.call(
            new Prompt("Who are you?")
        );
        System.out.println(response.getResult().getOutput().getText());
    }
}
