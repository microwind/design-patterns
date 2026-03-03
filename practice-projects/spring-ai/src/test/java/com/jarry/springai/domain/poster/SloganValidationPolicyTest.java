package com.jarry.springai.domain.poster;

import com.jarry.springai.domain.poster.policy.SloganValidationPolicy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SloganValidationPolicyTest {

    private final SloganValidationPolicy policy = new SloganValidationPolicy();

    @Test
    void shouldKeepOnlyValidChinese12CharLines() {
        List<String> filtered = policy.filterValid(List.of(
                "命运迷雾照见炽热初心之光",
                "孤城暗夜燃起不屈热血之火",
                "孤城暗夜燃起不屈热血之火",
                "TooShort",
                "含有标点，不允许"
        ), 2, 5);

        assertEquals(2, filtered.size());
    }

    @Test
    void shouldReturnEmptyWhenBelowMinCount() {
        List<String> filtered = policy.filterValid(List.of("命运迷雾照见炽热初心之光"), 2, 5);
        assertTrue(filtered.isEmpty());
    }
}
