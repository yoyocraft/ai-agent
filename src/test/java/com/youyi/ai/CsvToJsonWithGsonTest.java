package com.youyi.ai;

import com.youyi.ai.util.GsonUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/03
 */
class CsvToJsonWithGsonTest {

    private static final Logger logger = LoggerFactory.getLogger(CsvToJsonWithGsonTest.class);

    @Test
    void testCsvToJson() {
        String path = System.getProperty("user.dir") + "/src/test/resources/document/data.csv";
        String jsonOutputPath = System.getProperty("user.dir") + "/src/test/resources/document/data.json";
        String json = Assertions.assertDoesNotThrow(() -> convertCsvToJson(path));
        Assertions.assertNotNull(json);
        Assertions.assertDoesNotThrow(() -> writeJsonToFile(json, jsonOutputPath));
    }

    public String convertCsvToJson(String csvFilePath) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        String[] headers = null;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    // 处理表头
                    headers = parseCsvLine(line);
                    isFirstLine = false;
                } else {
                    // 处理数据行
                    String[] values = parseCsvLine(line);
                    if (headers.length != values.length) {
                        System.err.println("列数不匹配: " + line);
                        continue;
                    }

                    Map<String, Object> rowMap = new HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        String key = headers[i].trim();
                        String value = values[i].trim();

                        rowMap.put(key, convertValue(value));
                    }
                    dataList.add(rowMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 使用GSON转换为JSON
        return GsonUtil.toJson(dataList);
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i < line.length() - 1 && line.charAt(i + 1) == '"') {
                    // 转义引号
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());

        return values.toArray(new String[0]);
    }

    private Object convertValue(String value) {
        if (value.isEmpty()) {
            return null;
        }

        // 尝试转换为整数
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 不是整数，继续尝试其他类型
        }

        // 尝试转换为浮点数
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // 不是数字，继续尝试其他类型
        }

        // 尝试转换为布尔值
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }

        // 默认返回字符串
        return value;
    }

    private void writeJsonToFile(String jsonContent, String outputPath) throws IOException {
        // 确保目录存在
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (outputFile.exists()) {
            // del
            outputFile.delete();
        }

        // 写入文件
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(jsonContent);
        }
    }

    // 获取文件的绝对路径（用于显示）
    private String getAbsolutePath(String relativePath) {
        return Paths.get(relativePath).toAbsolutePath().toString();
    }
}
