package com.youyi.ai.demo.invoke;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/01
 */
public class HttpInvoke {

    public static void main(String[] args) {
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        String apiKey = System.getenv("DASHSCOPE_API_KEY");

        // 构建请求体
        JSONObject body = new JSONObject();
        body.set("model", "qwen-plus");

        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();

        messages.add(new JSONObject()
            .set("role", "system")
            .set("content", "You are a helpful assistant.")
        );
        messages.add(new JSONObject()
            .set("role", "user")
            .set("content", "who are you？")
        );

        input.set("messages", messages);
        body.set("input", input);

        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        body.set("parameters", parameters);

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", ContentType.JSON.getValue())
            .body(body.toString())
            .execute();

        // 输出响应内容
        System.out.println("Status: " + response.getStatus());
        System.out.println("Response: " + response.body());
    }
}
