package com.youyi.ai.app.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/04
 */
class WebScrapingToolTest {
    WebScrapingTool webScrapingTool = new WebScrapingTool();

    @Test
    void testScrapeWebPage() {
        String result = webScrapingTool.scrapeWebPage("https://newsnow.busiyi.world/");
        Assertions.assertNotNull(result);
    }
}

// Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme