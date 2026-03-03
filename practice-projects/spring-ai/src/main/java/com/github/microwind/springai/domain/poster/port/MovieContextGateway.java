package com.github.microwind.springai.domain.poster.port;

import com.github.microwind.springai.domain.poster.model.MovieContext;
import com.github.microwind.springai.domain.poster.model.MovieInfo;

public interface MovieContextGateway {
    MovieContext retrieve(MovieInfo movieInfo);
}
