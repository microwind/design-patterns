package com.jarry.springai.infra.crawler;

import com.jarry.springai.domain.poster.model.MovieContext;
import com.jarry.springai.domain.poster.model.MovieInfo;
import com.jarry.springai.domain.poster.model.SourceReference;
import com.jarry.springai.domain.poster.port.MovieContextGateway;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class SearchMovieContextGateway implements MovieContextGateway {

    private static final int TIMEOUT_MILLIS = (int) Duration.ofSeconds(8).toMillis();

    @Override
    public MovieContext retrieve(MovieInfo movieInfo) {
        String encodedTitle = UriUtils.encode(movieInfo.title(), StandardCharsets.UTF_8);
        String doubanUrl = "https://www.douban.com/search?q=" + encodedTitle;
        String baikeUrl = "https://baike.baidu.com/search/word?word=" + encodedTitle;

        List<SourceReference> sources = List.of(
                new SourceReference("douban", doubanUrl),
                new SourceReference("baike", baikeUrl)
        );

        List<String> snippets = new ArrayList<>();
        snippets.add("片名：" + movieInfo.title());
        snippets.add("简介：" + movieInfo.description());
        if (movieInfo.genre() != null && !movieInfo.genre().isBlank()) {
            snippets.add("类型：" + movieInfo.genre());
        }
        if (movieInfo.year() != null) {
            snippets.add("年份：" + movieInfo.year());
        }

        fetchPageText(doubanUrl).ifPresent(text -> snippets.add("豆瓣信息：" + text));
        fetchPageText(baikeUrl).ifPresent(text -> snippets.add("百科信息：" + text));

        String normalized = String.join("\n", snippets);
        if (normalized.length() > 1200) {
            normalized = normalized.substring(0, 1200);
        }
        return new MovieContext(normalized, sources);
    }

    private java.util.Optional<String> fetchPageText(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; MovieAI/1.0)")
                    .timeout(TIMEOUT_MILLIS)
                    .get();
            String text = doc.body().text().replaceAll("\\s+", " ").trim();
            if (text.length() > 500) {
                text = text.substring(0, 500);
            }
            return java.util.Optional.of(text);
        } catch (IOException ex) {
            return java.util.Optional.empty();
        }
    }
}
