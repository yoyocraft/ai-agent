package com.youyi.ai.agent;

import com.youyi.ai.BaseIT;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/07
 */
class ManusIT extends BaseIT {

    @Resource
    Manus manus;

    @Test
    void testManus() {
        String prompt = """
            我想要知道 https://newsnow.busiyi.world/ 这个网站的信息，写入到 newsnow 文件中。
            """;
        String result = manus.run(prompt);
        Assertions.assertNotNull(result);
    }
}
