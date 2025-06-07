package com.youyi.ai.agent;

import com.youyi.ai.app.advisor.LoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/07
 */
@Component
public class Manus extends ToolCallAgent {

    public Manus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        setName("Simple-Manus");
        final String SYSTEM_PROMPT = """  
            You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
            You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
            """;
        setSystemPrompt(SYSTEM_PROMPT);
        final String NEXT_STEP_PROMPT = """  
            Based on user needs, proactively select the most appropriate tool or combination of tools.  
            For complex tasks, you can break down the problem and use different tools step by step to solve it.  
            After using each tool, clearly explain the execution results and suggest the next steps.  
            If you want to stop the interaction at any point, use the `terminate` tool/function call.  
            """;
        setNextStepPrompt(NEXT_STEP_PROMPT);
        setMaxSteps(20);

        final ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
            .defaultAdvisors(new LoggerAdvisor())
            .build();
        setChatClient(chatClient);
    }
}


