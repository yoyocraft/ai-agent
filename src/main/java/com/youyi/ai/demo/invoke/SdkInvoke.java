package com.youyi.ai.demo.invoke;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.youyi.ai.util.GsonUtil;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * invoke ai using sdk
 *
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/01
 */
public class SdkInvoke {

    public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
            .role(Role.SYSTEM.getValue())
            .content("You are a helpful assistant.")
            .build();
        Message userMsg = Message.builder()
            .role(Role.USER.getValue())
            .content("who are you?")
            .build();
        GenerationParam param = GenerationParam.builder()
            .apiKey(System.getenv("DASHSCOPE_API_KEY"))
            .model("qwen-plus")
            .messages(Arrays.asList(systemMsg, userMsg))
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .build();
        return gen.call(param);
    }

    public static void main(String[] args) {
        try {
            assert StringUtils.isNotBlank(System.getenv("DASHSCOPE_API_KEY"));
            GenerationResult result = callWithMessage();
            System.out.println(GsonUtil.toJson(result));
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        System.exit(0);
    }
}
