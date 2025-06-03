package com.youyi.ai.app.rag;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

/**
 * @author <a href="https://github.com/yoyocraft">yoyocraft</a>
 * @date 2025/06/03
 */
class AppDocumentLoaderTest {

    AppDocumentLoader appDocumentLoader = new AppDocumentLoader();

    @Test
    void testLoadMarkdown() {
        List<Document> result = appDocumentLoader.loadMarkdown();
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }
}

// Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme