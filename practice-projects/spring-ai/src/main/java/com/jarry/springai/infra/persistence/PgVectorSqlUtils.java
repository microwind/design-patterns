package com.jarry.springai.infra.persistence;

import java.util.StringJoiner;

public final class PgVectorSqlUtils {

    private PgVectorSqlUtils() {
    }

    public static String toVectorLiteral(float[] vector) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (float value : vector) {
            joiner.add(Float.toString(value));
        }
        return joiner.toString();
    }
}
