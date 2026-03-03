package com.github.microwind.springai.infrastructure.llm;

import com.github.microwind.springai.domain.poster.model.MovieContext;
import com.github.microwind.springai.domain.poster.model.MovieInfo;
import com.github.microwind.springai.domain.poster.port.PosterSloganGenerator;
import com.github.microwind.springai.infrastructure.config.PromptTemplateLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SpringAiPosterSloganGenerator implements PosterSloganGenerator {

    private final ChatClient chatClient;
    private final PromptTemplateLoader promptLoader;

    public SpringAiPosterSloganGenerator(ChatClient.Builder chatClientBuilder, PromptTemplateLoader promptLoader) {
        this.chatClient = chatClientBuilder.build();
        this.promptLoader = promptLoader;
    }

    @Override
    public List<String> generateCandidates(MovieInfo movieInfo, MovieContext movieContext, int candidateCount) {
        String systemPrompt = promptLoader.load("prompts/poster-system.md");
        String userPrompt = """
                片名：%s
                简介：%s
                上下文：%s
                风格偏好：%s
                生成数量：%d
                """.formatted(
                movieInfo.title(),
                movieInfo.description(),
                movieContext.normalizedContext(),
                movieInfo.tone() == null ? "通用电影海报风格" : movieInfo.tone(),
                candidateCount
        );

        String content = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        if (content == null || content.isBlank()) {
            return List.of();
        }

        return Arrays.stream(content.split("\\R"))
                .map(String::trim)
                .map(line -> line.replaceFirst("^[0-9]+[.、]\\s*", ""))
                .filter(line -> !line.isBlank())
                .toList();
    }
}
