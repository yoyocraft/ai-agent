package com.youyi.ai.app.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/04
 */
// TODO: https://www.searchapi.io/  https://www.searchapi.io/baidu
public class WebSearchTool {

    @Tool(description = "Search web")
    public String searchWeb(@ToolParam(description = "The query to search") String query) {
        // MOCK
        return "https://www.google.com/search?q=" + query;
    }
}
