package com.youyi.ai.app.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/05
 */
public class TerminalOperationTool {

    @Tool(description = "Execute a command in the terminal")
    public String exec(@ToolParam(description = "The command to execute") String command) {
        StringBuilder output = new StringBuilder();
        try {
            // UNIX
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            output.append("Exit code: ").append(exitCode);
            if (exitCode != 0) {
                output.append("Command failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }

        return output.toString();
    }
}
