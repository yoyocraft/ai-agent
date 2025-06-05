package com.youyi.ai.app.tool;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/04
 */
class FileOperationToolTest {
    FileOperationTool fileOperationTool = new FileOperationTool();

    @Test
    void testReadFile() {
        String result = fileOperationTool.readFile("filename");
        Assertions.assertEquals("content", result);
    }

    @Test
    void testWriteFile() {
        String result = fileOperationTool.writeFile("filename", "content");
        Assertions.assertEquals("File written successfully to " + fileOperationTool.buildFilePath("filename"), result);
    }
}

// Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme