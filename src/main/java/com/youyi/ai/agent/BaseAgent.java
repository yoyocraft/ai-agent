package com.youyi.ai.agent;

import com.youyi.ai.agent.model.AgentState;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

/**
 * Abstract base class for managing agent state and execution.
 * <p>
 * Provides foundational functionality for state transitions, memory management,
 * and a step-based execution loop. Subclasses must implement the `step` method.
 *
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/07
 */
@Getter
@Setter
public abstract class BaseAgent {

    private static final Logger logger = LoggerFactory.getLogger(BaseAgent.class);

    // properties
    private String name;

    // prompt
    private String systemPrompt;
    private String nextStepPrompt;

    // state
    private AgentState state = AgentState.IDLE;

    // steps
    private int maxSteps = 10;
    private int currentStep = 0;

    // LLM
    private ChatClient chatClient;

    // memory
    private List<Message> messages = new ArrayList<>();

    // core methods
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new IllegalStateException("Agent is not idle");
        }

        if (StringUtils.isBlank(userPrompt)) {
            throw new IllegalArgumentException("User prompt cannot be blank");
        }

        // change state
        this.state = AgentState.RUNNING;

        // record message
        messages.add(new UserMessage(userPrompt));

        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                this.currentStep = i + 1;
                logger.info("Execute step {}/{}", currentStep, maxSteps);
                String stepResult = step();
                String result = String.format("Step %d: %s", currentStep, stepResult);
                results.add(result);
            }
            if (currentStep >= maxSteps) {
                this.state = AgentState.FINISHED;
                results.add("Max steps reached.");
            }

            return String.join("\n", results);
        } catch (Exception e) {
            this.state = AgentState.ERROR;
            logger.error("Agent error, ", e);
            return "Agent error: " + e.getMessage();
        } finally {
            cleanup();
        }
    }

    // hooks

    /**
     * execute one step
     */
    protected abstract String step();

    protected void cleanup() {
        // subclass can override this method to clean up resources
    }
}
