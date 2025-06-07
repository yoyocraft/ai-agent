package com.youyi.ai.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.youyi.ai.agent.model.AgentState;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/07
 */
@Getter
@Setter
public class ToolCallAgent extends ReActAgent {

    private static final Logger logger = LoggerFactory.getLogger(ToolCallAgent.class);

    private final ToolCallback[] availableTools;
    private final ToolCallingManager toolCallingManager;
    private final ChatOptions chatOptions;

    private ChatResponse toolCallResponse;

    public ToolCallAgent(ToolCallback[] tools) {
        super();
        this.availableTools = tools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 禁用 Spring AI 内置的工具代理，本类实现工具代理
        this.chatOptions = DashScopeChatOptions.builder()
            .withProxyToolCalls(true)
            .build();
    }

    @Override
    protected boolean think() {
        List<Message> messages = getMessages();
        if (StringUtils.isNotBlank(getNextStepPrompt())) {
            messages.add(new UserMessage(getNextStepPrompt()));
        }
        Prompt prompt = new Prompt(messages, this.chatOptions);

        try {
            this.toolCallResponse = getChatClient()
                .prompt(prompt)
                .system(getSystemPrompt())
                .tools(availableTools)
                .call()
                .chatResponse();
            // 记录相应，用于 Act
            AssistantMessage assistantMessage = this.toolCallResponse.getResult().getOutput();
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            logger.info("[{}]的思考:{}", getName(), result);
            logger.info("[{}]选择了{}个工具", getName(), toolCalls.size());
            String toolCallInfo = toolCalls.stream()
                .map(toolCall -> String.format("工具%s: %s", toolCall.name(), toolCall.arguments()))
                .collect(Collectors.joining("\n"));
            logger.info(toolCallInfo);

            if (toolCallInfo.isEmpty()) {
                // 不调用工具，记录 AI 消息，维护上下文
                messages.add(assistantMessage);
                return false;
            }
            // 需要调用工具
            return true;
        } catch (Exception e) {
            logger.error("[{}]思考过程遇到了问题:{}", getName(), e.getMessage());
            messages.add(new AssistantMessage("处理时遇到错误:" + e.getMessage()));
            return false;
        }
    }

    @Override
    protected String act() {
        if (Objects.isNull(toolCallResponse) || !toolCallResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        // call tool
        Prompt prompt = new Prompt(getMessages(), this.chatOptions);
        ToolExecutionResult toolCallResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        // 记录消息上下文
        setMessages(toolCallResult.conversationHistory());
        // 当前工具的调用结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolCallResult.conversationHistory());
        String result = toolResponseMessage.getResponses()
            .stream()
            .map(response -> String.format("%s: %s", response.name(), response.responseData()))
            .collect(Collectors.joining("\n"));
        // 判断是否调用了终止工具
        boolean terminated = toolResponseMessage.getResponses()
            .stream()
            .anyMatch(resp -> "terminate".equalsIgnoreCase(resp.name()));
        if (terminated) {
            setState(AgentState.FINISHED);
        }
        logger.info(result);
        return result;
    }
}
