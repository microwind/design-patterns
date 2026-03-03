package com.github.microwind.springai.infrastructure.llm;

import com.github.microwind.springai.domain.rag.model.RagQuery;
import com.github.microwind.springai.domain.rag.model.RetrievedChunk;
import com.github.microwind.springai.domain.rag.port.RagAnswerGenerator;
import com.github.microwind.springai.infrastructure.config.PromptTemplateLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringAiRagAnswerGenerator implements RagAnswerGenerator {

    private final ChatClient chatClient;
    private final PromptTemplateLoader promptLoader;

    public SpringAiRagAnswerGenerator(ChatClient.Builder chatClientBuilder, PromptTemplateLoader promptLoader) {
        this.chatClient = chatClientBuilder.build();
        this.promptLoader = promptLoader;
    }

    @Override
    public String generateGrounded(RagQuery query, List<RetrievedChunk> chunks) {
        String systemPrompt = promptLoader.load("prompts/rag-grounded.md");

        String evidenceBlock = chunks.stream()
                .map(chunk -> "chunk_id=%d score=%.4f title=%s text=%s".formatted(
                        chunk.chunkId(),
                        chunk.similarity(),
                        chunk.documentTitle(),
                        chunk.content()))
                .reduce((a, b) -> a + "\n" + b)
                .orElse("无证据");

        String userPrompt = """
                问题：%s
                证据：
                %s
                """.formatted(query.question(), evidenceBlock);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    @Override
    public String generateFallback(RagQuery query) {
        String systemPrompt = promptLoader.load("prompts/rag-fallback.md");
        return chatClient.prompt()
                .system(systemPrompt)
                .user("问题：" + query.question())
                .call()
                .content();
    }
}
