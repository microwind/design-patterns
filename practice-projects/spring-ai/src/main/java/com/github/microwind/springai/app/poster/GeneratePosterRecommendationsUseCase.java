package com.github.microwind.springai.app.poster;

import com.github.microwind.springai.domain.poster.model.MovieContext;
import com.github.microwind.springai.domain.poster.model.MovieInfo;
import com.github.microwind.springai.domain.poster.model.PosterRecommendationResult;
import com.github.microwind.springai.domain.poster.policy.SloganValidationPolicy;
import com.github.microwind.springai.domain.poster.port.MovieContextGateway;
import com.github.microwind.springai.domain.poster.port.PosterSloganGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneratePosterRecommendationsUseCase {

    private final MovieContextGateway contextGateway;
    private final PosterSloganGenerator sloganGenerator;
    private final PosterProperties properties;
    private final SloganValidationPolicy validationPolicy = new SloganValidationPolicy();

    public GeneratePosterRecommendationsUseCase(
            MovieContextGateway contextGateway,
            PosterSloganGenerator sloganGenerator,
            PosterProperties properties
    ) {
        this.contextGateway = contextGateway;
        this.sloganGenerator = sloganGenerator;
        this.properties = properties;
    }

    public PosterRecommendationResult execute(MovieInfo movieInfo) {
        MovieContext movieContext = contextGateway.retrieve(movieInfo);

        List<String> firstRound = sloganGenerator.generateCandidates(
                movieInfo,
                movieContext,
                properties.candidateCount()
        );

        List<String> validated = validationPolicy.filterValid(
                firstRound,
                properties.minFinalCount(),
                properties.maxFinalCount()
        );

        if (validated.isEmpty()) {
            List<String> retryRound = sloganGenerator.generateCandidates(
                    movieInfo,
                    movieContext,
                    properties.candidateCount() * 2
            );
            validated = validationPolicy.filterValid(
                    retryRound,
                    properties.minFinalCount(),
                    properties.maxFinalCount()
            );
        }

        if (validated.isEmpty()) {
            throw new IllegalStateException("Cannot generate enough valid 12-char slogans");
        }

        return new PosterRecommendationResult(movieInfo.title(), validated, movieContext.sources());
    }
}
