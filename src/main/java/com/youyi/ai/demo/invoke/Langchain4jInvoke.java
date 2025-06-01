package com.youyi.ai.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/01
 */
public class Langchain4jInvoke {

    public static void main(String[] args) {
        ChatModel qwenModel = QwenChatModel.builder()
            .apiKey(System.getenv("DASHSCOPE_API_KEY"))
            .modelName("qwen-plus")
            .build();
        String response = qwenModel.chat("hello, I'm yoyocraft. Is my name beautiful?");
        System.out.println(response);
    }
}
