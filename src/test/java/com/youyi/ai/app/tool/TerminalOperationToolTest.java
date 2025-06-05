package com.youyi.ai.app.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/05
 */
class TerminalOperationToolTest {
    TerminalOperationTool terminalOperationTool = new TerminalOperationTool();

    @Test
    void testExec() {
        String result = terminalOperationTool.exec("ls -l");
        Assertions.assertTrue(result.contains("Exit code: 0"));
    }
}

// Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme