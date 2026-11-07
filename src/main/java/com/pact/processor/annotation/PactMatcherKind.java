package com.pact.processor.annotation;

/** Pact matcher categories supported by {@link PactMatcher}. */
public enum PactMatcherKind {
    /** Select an appropriate matcher based on the field type. */
    AUTO,
    /** Match the supplied example exactly. */
    EQUALS,
    /** Match a string-like value. */
    STRING_TYPE,
    /** Match a numeric value. */
    NUMBER_TYPE,
    /** Match an integer value within Pact's integer example range. */
    INTEGER_TYPE,
    /** Match a decimal number. */
    DECIMAL_TYPE,
    /** Match a boolean value. */
    BOOLEAN_TYPE,
    /** Match a string using a regular expression. */
    REGEX,
    /** Match a date. */
    DATE,
    /** Match a time. */
    TIME,
    /** Match a date-time. */
    DATETIME,
    /** Match an IPv4 address. */
    IP_V4,
    /** Match an integer identifier. */
    ID,
    /** Match a hexadecimal string. */
    HEX,
    /** Match a UUID. */
    UUID,
    /** Require a string to include the configured example. */
    INCLUDES,
    /** Match a URL assembled from the configured base URL and path fragments. */
    URL,
    /** Require a null value. */
    NULL_VALUE
}
