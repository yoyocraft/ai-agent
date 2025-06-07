package com.youyi.ai.app.tool;

import org.springframework.ai.tool.annotation.Tool;

/**
 * Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.
 * When you have finished all the tasks, call this tool to end the work.
 *
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/07
 */
public class TerminateTool {

    @Tool(description = "Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.")
    public String terminate() {
        return "The interaction has been completed";
    }
}
