package com.youyi.ai.app.tool;

import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/05
 */
@Configuration
public class ToolRegistrar {

    @Bean
    public ToolCallback[] allTools() {
        return ToolCallbacks.from(
            new TerminalOperationTool(),
            // new WebSearchTool(),
            new WebScrapingTool(),
            new FileOperationTool(),
            new TerminateTool()
        );
    }
}
