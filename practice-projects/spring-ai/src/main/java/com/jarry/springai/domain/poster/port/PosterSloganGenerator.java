package com.jarry.springai.domain.poster.port;

import com.jarry.springai.domain.poster.model.MovieContext;
import com.jarry.springai.domain.poster.model.MovieInfo;

import java.util.List;

public interface PosterSloganGenerator {
    List<String> generateCandidates(MovieInfo movieInfo, MovieContext movieContext, int candidateCount);
}
