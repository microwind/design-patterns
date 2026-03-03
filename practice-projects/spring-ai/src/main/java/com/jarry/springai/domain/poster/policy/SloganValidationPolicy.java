package com.jarry.springai.domain.poster.policy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SloganValidationPolicy {

    private static final Pattern ONLY_CJK = Pattern.compile("^[\\u4e00-\\u9fff]+$");

    public List<String> filterValid(List<String> candidates, int minCount, int maxCount) {
        Set<String> deduped = new LinkedHashSet<>();
        for (String candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            String line = candidate.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (!ONLY_CJK.matcher(line).matches()) {
                continue;
            }
            if (line.length() != 12) {
                continue;
            }
            deduped.add(line);
            if (deduped.size() == maxCount) {
                break;
            }
        }
        if (deduped.size() < minCount) {
            return List.of();
        }
        return new ArrayList<>(deduped);
    }
}
