package com.jarry.springai.domain.poster.port;

import com.jarry.springai.domain.poster.model.MovieContext;
import com.jarry.springai.domain.poster.model.MovieInfo;

public interface MovieContextGateway {
    MovieContext retrieve(MovieInfo movieInfo);
}
