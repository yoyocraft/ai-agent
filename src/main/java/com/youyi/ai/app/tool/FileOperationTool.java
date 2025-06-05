package com.youyi.ai.app.tool;

import cn.hutool.core.io.FileUtil;
import com.youyi.ai.app.constant.SystemConstant;
import java.io.File;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/04
 */
public class FileOperationTool {

    private static final String FILE_DIR = SystemConstant.TMP_FILE_PATH + File.separator + "file";

    @Tool(description = "Read content from a content")
    public String readFile(@ToolParam(description = "Name of the file to read") String filename) {
        String filePath = buildFilePath(filename);
        return FileUtil.readUtf8String(filePath);
    }

    @Tool(description = "Write content to a file")
    public String writeFile(
        @ToolParam(description = "Name of the file to write") String filename,
        @ToolParam(description = "Content to write") String content
    ) {
        String filePath = buildFilePath(filename);
        FileUtil.writeUtf8String(content, filePath);
        return "File written successfully to " + filePath;
    }

    protected String buildFilePath(String filename) {
        return FILE_DIR + File.separator + filename;
    }
}
