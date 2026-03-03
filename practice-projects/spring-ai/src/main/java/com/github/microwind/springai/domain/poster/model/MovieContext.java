package com.github.microwind.springai.domain.poster.model;

import java.util.List;

public record MovieContext(String normalizedContext, List<SourceReference> sources) {
}
