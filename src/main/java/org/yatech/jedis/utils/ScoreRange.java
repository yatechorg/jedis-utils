package org.yatech.jedis.utils;

/**
 * <p>Created on 03/06/16
 *
 * @author Yinon Avraham
 */
public class ScoreRange {
    private static final String MIN_INF = "-inf";
    private static final String MAX_INF = "+inf";

    private final String from;
    private final String to;
    private final int offset;
    private final int count;

    public ScoreRange(String from, String to, int offset, int count) {
        this.from = from;
        this.to = to;
        this.offset = offset;
        this.count = count;
        if ((offset < 0 && count >= 0) || (offset >= 0 && count < 0)) {
            throw new IllegalArgumentException("offset and count must be both specified or not together");
        }
    }

    public String from() {
        return from;
    }

    public String fromReverse() {
        return MIN_INF.equals(from) ? MAX_INF : from;
    }

    public String to() {
        return to;
    }

    public String toReverse() {
        return MAX_INF.equals(to) ? MIN_INF : to;
    }

    public int offset() {
        return offset;
    }

    public int count() {
        return count;
    }

    public boolean hasLimit() {
        return offset >= 0 && count >= 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder from(double from, boolean inclusive) {
        return builder().from(from, inclusive);
    }

    public static Builder fromInfinity() {
        return builder().fromInfinity();
    }

    public static class Builder {
        private String from = MIN_INF;
        private String to = MAX_INF;
        private int offset = -1;
        private int count = -1;

        public Builder from(double from, boolean inclusive) {
            this.from = (inclusive ? "" : "(") + String.valueOf(from);
            return this;
        }

        public Builder fromInfinity() {
            this.from = MIN_INF;
            return this;
        }

        public Builder to(double to, boolean inclusive) {
            this.to = (inclusive ? "" : "(") + String.valueOf(to);
            return this;
        }

        public Builder toInfinity() {
            this.to = MAX_INF;
            return this;
        }

        public Builder limit(int offset, int count) {
            this.offset = offset;
            this.count = count;
            return this;
        }

        public ScoreRange build() {
            return new ScoreRange(from, to, offset, count);
        }
    }
}
