package com.youyi.ai.app.tool;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/04
 */
public class WebScrapingTool {

    private static final Logger logger = LoggerFactory.getLogger(WebScrapingTool.class);

    @Tool(description = "Scrape web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Connection connect = Jsoup.connect(url);
            Document webDocument = connect.get();
            return webDocument.html();
        } catch (IOException e) {
            logger.error("scrape web page error, url:{}", url, e);
            return "Error occurred while scraping web page, url:" + url;
        }
    }
}
