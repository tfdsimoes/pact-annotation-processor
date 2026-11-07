package com.pact.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-retained field annotation selecting a Pact matcher for a supported model property and
 * supplying any matcher-specific arguments.
 *
 * <p>Arguments that do not apply to the selected matcher, or are invalid for the field type, are
 * reported as compile-time errors. Supported matcher/type combinations are described in the README.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PactMatcher {
    /**
     * Matcher kind; {@link PactMatcherKind#AUTO} selects the type-appropriate default.
     *
     * @return the matcher kind
     */
    PactMatcherKind value() default PactMatcherKind.AUTO;

    /**
     * Optional matcher example. If {@link PactExample#value()} is also supplied, both values must
     * agree; conflicting values are reported as compile-time errors.
     *
     * @return the example, or an empty string when no matcher example is supplied
     */
    String example() default "";

    /**
     * Required pattern for {@link PactMatcherKind#REGEX}.
     *
     * @return the regular expression, or an empty string when unused
     */
    String regex() default "";

    /**
     * Optional date/time pattern for temporal matchers.
     *
     * @return the temporal pattern, or an empty string to use the default
     */
    String format() default "";

    /**
     * Base URL argument for {@link PactMatcherKind#URL}.
     *
     * @return the base URL, or an empty string when unused
     */
    String baseUrl() default "";

    /**
     * Path-fragment arguments for {@link PactMatcherKind#URL}.
     *
     * @return URL path fragments, or an empty array when unused
     */
    String[] pathFragments() default {};
}
