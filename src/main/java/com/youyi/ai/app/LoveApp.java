package com.youyi.ai.app;

import com.youyi.ai.app.advisor.LoggerAdvisor;
import com.youyi.ai.app.memory.InFileChatMemory;
import com.youyi.ai.util.GsonUtil;
import jakarta.annotation.Resource;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/02
 */
@Component
public class LoveApp {
    private static final Logger logger = LoggerFactory.getLogger(LoveApp.class);

    private static final String LOVE_APP_SYSTEM_PROMPT = """
        【角色设定】资深恋爱顾问，10年经验，擅长心理学与实战结合，风格温暖专业。
        【交互原则】
        1)渐进提问：每轮1-2个开放式问题，如"你提到争吵，具体哪个场景最难受？"，用漏斗式提问
        2)三维分析：自动拆解用户问题至行为模式、情感需求、认知偏差
        3)结构化建议：复述处境，提供新视角，举例说明，给3条行动建议，总结要点
        【流程控制】
        1)初始：用共情语句建立信任
        2)中期：询问关系信心指数及原因
        3)终局：提供3天破冰、3周优化、3月成长的解决方案
        【禁忌】
        不直接建议分手
        不假设用户性别或性取向
        不使用非科学判断依据
        【话术风格】
        温和专业，像朋友一样倾听，像专家一样分析""";

    private static final String CHAT_MEMORY_PATH = System.getProperty("user.dir") + File.separator + "chats";

    private final ChatClient chatClient;

    @Resource
    private VectorStore appVectorStore;

    @Resource
    private Advisor appDashscopeRagAdvisor;

    public LoveApp(ChatModel dashscopeChatModel) {
        chatClient = ChatClient.builder(dashscopeChatModel)
            .defaultSystem(LOVE_APP_SYSTEM_PROMPT)
            .defaultAdvisors(
                new MessageChatMemoryAdvisor(new InFileChatMemory(CHAT_MEMORY_PATH)),
                new LoggerAdvisor()
                // new ReReadingAdvisor()
            )
            .build();
    }

    public String chat(String message, String chatId) {
        ChatResponse response = chatClient
            .prompt()
            .user(message)
            .advisors(
                spec -> spec
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
            )
            .call()
            .chatResponse();
        return response.getResult().getOutput().getText();
    }

    public LoveReport chatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
            .prompt()
            .system(LOVE_APP_SYSTEM_PROMPT + "\n每次对话之后生成恋爱结果，标题为{用户名}恋爱报告，内容为建议列表")
            .user(message)
            .advisors(
                spec -> spec
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
            )
            .call()
            .entity(LoveReport.class);
        logger.info("love report: {}", GsonUtil.toJson(loveReport));
        return loveReport;
    }

    public String chatWithRag(String message, String chatId) {
        ChatResponse response = chatClient
            .prompt()
            .user(message)
            .advisors(
                spec -> spec
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
            )
            .advisors(new QuestionAnswerAdvisor(appVectorStore))
            .call()
            .chatResponse();
        return response.getResult().getOutput().getText();
    }

    public String chatWithDashscopeRag(String message, String chatId) {
        ChatResponse response = chatClient
            .prompt()
            .user(message)
            .advisors(
                spec -> spec
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
            )
            .advisors(appDashscopeRagAdvisor)
            .call()
            .chatResponse();
        return response.getResult().getOutput().getText();
    }

}
