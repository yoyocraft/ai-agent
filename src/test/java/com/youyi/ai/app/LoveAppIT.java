package com.youyi.ai.app;

import cn.hutool.core.util.IdUtil;
import com.youyi.ai.BaseIT;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/02
 */
class LoveAppIT extends BaseIT {

    private static final Logger logger = LoggerFactory.getLogger(LoveAppIT.class);

    @Resource
    LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = IdUtil.nanoId();
        // turn 1
        String message = "你好，我是游艺";
        String response = loveApp.chat(message, chatId);
        Assertions.assertNotNull(response);
        // turn 2
        message = "我想让另一半（Money）更喜欢我";
        response = loveApp.chat(message, chatId);
        Assertions.assertNotNull(response);
    }

    @Test
    void testChatWithMemory() {
        String chatId = "JYVuxnqaLiY4r2ltqTVU_";
        String message = "你好，我是谁？";
        String response = loveApp.chat(message, chatId);
        Assertions.assertNotNull(response);
    }

    @Test
    void testChatWithReport() {
        String chatId = IdUtil.nanoId();
        String message = "你好，我是游艺，我想让另一半更喜欢我，但我不知道怎么做。";
        LoveReport report = loveApp.chatWithReport(message, chatId);
        Assertions.assertNotNull(report);
    }

    @Test
    void testChatWithRag() {
        String chatId = IdUtil.simpleUUID();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String response = loveApp.chatWithRag(message, chatId);
        Assertions.assertNotNull(response);
    }
}
