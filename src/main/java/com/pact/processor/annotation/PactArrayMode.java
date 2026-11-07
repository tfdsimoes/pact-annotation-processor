package com.pact.processor.annotation;

/** Pact matching modes supported for arrays and collections. */
public enum PactArrayMode {
    /** Match each item using a representative example. */
    EACH_LIKE,
    /** Match at least the configured minimum number of items. */
    MIN_LIKE,
    /** Match at most the configured maximum number of items. */
    MAX_LIKE,
    /** Match a size bounded by both configured minimum and maximum. */
    MIN_MAX_LIKE,
    /** Match items without requiring their order. */
    UNORDERED,
    /** Match unordered items with a minimum size. */
    UNORDERED_MIN,
    /** Match unordered items with a maximum size. */
    UNORDERED_MAX,
    /** Match unordered items with both a minimum and maximum size. */
    UNORDERED_MIN_MAX,
    /** Require one or more alternatives to match; supported on object-root array properties. */
    ARRAY_CONTAINS
}
