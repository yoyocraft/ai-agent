package com.youyi.ai.app.rag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/03
 */
@Component
public class AppDocumentLoader {

    private static final Logger logger = LoggerFactory.getLogger(AppDocumentLoader.class);

    private static final String MARKDOWN_PATH = "classpath:document/*.md";

    private static final String JSON_PATH = "classpath:person/*.json";

    private static final String METADATA_FILENAME = "filename";

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());

    public List<Document> loadMarkdown() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources(MARKDOWN_PATH);
            for (Resource resource : resources) {
                List<Document> documents = doLoadMarkdown(resource);
                allDocuments.addAll(documents);
            }
        } catch (IOException e) {
            logger.error("load markdown error, path:{}", MARKDOWN_PATH, e);
        }
        return allDocuments;
    }

    public List<Document> loadJson() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources(JSON_PATH);
            for (Resource resource : resources) {
                List<Document> documents = doLoadJson(resource);
                allDocuments.addAll(documents);
            }
        } catch (IOException e) {
            logger.error("load csv error, path:{}", JSON_PATH, e);
        }
        return allDocuments;
    }

    private List<Document> doLoadMarkdown(final Resource resource) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(false)
            .withIncludeBlockquote(false)
            .withAdditionalMetadata(METADATA_FILENAME, resource.getFilename())
            .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
        return reader.get();
    }

    private List<Document> doLoadJson(final Resource resource) {
        JsonReader jsonReader = new JsonReader(resource);
        return jsonReader.get();
    }
}
