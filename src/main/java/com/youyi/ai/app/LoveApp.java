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
import org.springframework.ai.tool.ToolCallback;
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

    private static final String LOOK_FOR_ROMANTIC_PARTNER_PROMPT = """
        你是一个智能恋爱匹配助手，请根据用户的要求从知识库中寻找最适合的恋爱对象。请严格按照以下规则执行：
        1. 匹配规则：
           - 最多返回5个最匹配的候选人
           - 必须严格遵守用户指定的性别要求（男/女）
           - 必须满足用户提出的核心条件（如年龄范围、身高要求等硬性条件）
           - 对于非硬性条件（如兴趣爱好、职业等），按匹配度排序
        2. 输出格式要求：
           - 如果找到匹配对象：
             [候选人1]: [姓名]，[年龄]岁，[性别]，[身高]，[职业]，[兴趣爱好]，匹配度：[X%]
             [候选人2]: [姓名]，[年龄]岁，[性别]，[身高]，[职业]，[兴趣爱好]，匹配度：[X%]
             ...
             *共找到X位匹配对象*
           - 如果找不到任何匹配对象：
             "未找到符合条件的匹配对象，请联系管理员更新知识库"
        3. 匹配度计算标准：
           - 硬性条件（性别、年龄等）必须100%匹配
           - 其他条件按重要性加权计算（用户明确强调的条件权重更高）
           - 兴趣爱好等软性条件按重合度计算
        4. 特别要求：
           - 性别必须明确匹配，不可模糊处理
           - 如果用户要求"正常"性别，默认为男女二元性别
           - 不得编造知识库中不存在的信息
        5. 示例响应：
           用户输入：寻找25-30岁之间，身高170cm以上，喜欢旅行的女性
           正确响应：
           [候选人1]: 张雨晴，27岁，女，172cm，设计师，旅行/摄影/咖啡，匹配度：92%
           [候选人2]: 李思思，28岁，女，175cm，市场专员，徒步/美食/旅行，匹配度：88%
           *共找到2位匹配对象*
           用户输入：寻找40岁以上，身高190cm的男性
           正确响应：
           "未找到符合条件的匹配对象，请联系管理员更新知识库\"""";
    private static final String CHAT_MEMORY_PATH = System.getProperty("user.dir") + File.separator + "chats";

    private final ChatClient chatClient;

    @Resource
    private VectorStore appVectorStore;

    @Resource
    private Advisor appDashscopeRagAdvisor;

    @Resource
    private VectorStore romanticPartnerVectorStore;

    @Resource
    private ToolCallback[] allTools;

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

    public String chatForRomanticPartner(String message, String chatId) {
        ChatResponse response = chatClient
            .prompt()
            .system(LOOK_FOR_ROMANTIC_PARTNER_PROMPT)
            .user(message)
            .advisors(
                spec -> spec
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
            )
            .advisors(new QuestionAnswerAdvisor(romanticPartnerVectorStore))
            .call()
            .chatResponse();
        return response.getResult().getOutput().getText();
    }

    public String chatWithTool(String message, String chatId) {
        ChatResponse response = chatClient
            .prompt()
            .system("智能助手")
            .user(message)
            .advisors(
                spec -> spec
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
            )
            .tools(allTools)
            .call()
            .chatResponse();
        return response.getResult().getOutput().getText();
    }

}
