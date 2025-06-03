package com.youyi.ai.core;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.PromptTemplate;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/02
 */
class SpringAiUnitTest {
    @Test
    void testPromptTemplate() {
        // 定义带有变量的模板
        String template = "Hello，{name}. Today is {day}, {weather}。";

        // 创建模板对象
        PromptTemplate promptTemplate = new PromptTemplate(template);

        // 准备变量映射
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "yoyocraft");
        variables.put("day", "Monday");
        variables.put("weather", "cloudy");

        // 生成最终提示文本
        String prompt = promptTemplate.render(variables);

        Assertions.assertEquals("Hello，yoyocraft. Today is Monday, cloudy。", prompt);
    }
}
