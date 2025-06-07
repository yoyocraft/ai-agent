package com.youyi.ai.agent;

/**
 * Reasoning And Act Agent
 *
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/07
 */
public abstract class ReActAgent extends BaseAgent {

    @Override
    protected String step() {
        if (!think()) {
            return "Thinking complete - no action needed";
        }
        return act();
    }

    // hooks
    protected abstract boolean think();

    protected abstract String act();
}
