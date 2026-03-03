package com.jarry.springai.interfaces.rest.poster;

import com.jarry.springai.app.poster.GeneratePosterRecommendationsUseCase;
import com.jarry.springai.domain.poster.model.MovieInfo;
import com.jarry.springai.domain.poster.model.PosterRecommendationResult;
import com.jarry.springai.interfaces.rest.dto.PosterRecommendationRequest;
import com.jarry.springai.interfaces.rest.dto.PosterRecommendationResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/poster")
public class PosterRecommendationController {

    private final GeneratePosterRecommendationsUseCase useCase;

    public PosterRecommendationController(GeneratePosterRecommendationsUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/recommendations")
    public PosterRecommendationResponse generate(@Valid @RequestBody PosterRecommendationRequest request) {
        PosterRecommendationResult result = useCase.execute(new MovieInfo(
                request.movieTitle(),
                request.movieDescription(),
                request.year(),
                request.genre(),
                request.tone()
        ));

        List<PosterRecommendationResponse.SourceDto> sources = result.sources().stream()
                .map(source -> new PosterRecommendationResponse.SourceDto(source.site(), source.url()))
                .toList();

        return new PosterRecommendationResponse(result.movieTitle(), result.recommendations(), sources);
    }
}
