package com.youyi.ai.memory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/02
 */
public class InFileChatMemory implements ChatMemory {

    private static final Logger logger = LoggerFactory.getLogger(InFileChatMemory.class);

    private static final Kryo KRYO = new Kryo();

    private static final String FILE_SUFFIX = ".kryo";

    static {
        KRYO.setRegistrationRequired(false);
        KRYO.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    private final String baseDir;

    public InFileChatMemory(String dir) {
        this.baseDir = dir;
        mkdirIfNeed(dir);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        conversationMessages.addAll(messages);
        saveConversation(conversationId, conversationMessages);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> messages = getOrCreateConversation(conversationId);
        return messages.stream()
            .skip(Math.max(messages.size() - lastN, 0))
            .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (!file.exists()) {
            return;
        }
        if (!file.delete()) {
            logger.error("Error deleting conversation file:{},", conversationId);
        }
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            KRYO.writeObject(output, messages);
        } catch (IOException e) {
            logger.error("Error saving conversation:{},", conversationId, e);
        }
    }

    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = KRYO.readObject(input, ArrayList.class);
            } catch (IOException e) {
                logger.error("Error reading conversation file:{},", conversationId, e);
            }
        }
        return messages;
    }

    private File getConversationFile(String conversationId) {
        return new File(baseDir, conversationId + FILE_SUFFIX);
    }

    private void mkdirIfNeed(String dir) {
        File file = new File(dir);
        if (file.exists()) {
            return;
        }
        if (!file.mkdirs()) {
            throw new RuntimeException("Error creating directory: " + dir);
        }
    }

}
