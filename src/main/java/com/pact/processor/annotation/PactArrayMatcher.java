package com.pact.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-retained field annotation configuring Pact matching for an array or a {@code List},
 * {@code Collection}, or {@code Set} property.
 *
 * <p>Array size constraints are supplied only through {@link #minSize()} and {@link #maxSize()}.
 * The default {@link PactArrayMode#EACH_LIKE} mode is promoted to a bounded mode when a size is set.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PactArrayMatcher {
    /**
     * Array matching mode.
     *
     * @return the selected mode
     */
    PactArrayMode value() default PactArrayMode.EACH_LIKE;

    /**
     * Minimum example/matching size, or {@code -1} when unspecified.
     *
     * @return the minimum size
     */
    int minSize() default -1;

    /**
     * Maximum example/matching size, or {@code -1} when unspecified.
     *
     * @return the maximum size
     */
    int maxSize() default -1;

    /**
     * Scalar or enum examples required by {@link PactArrayMode#ARRAY_CONTAINS}.
     *
     * @return the alternative scalar examples
     */
    String[] contains() default {};

    /**
     * Fully qualified model types required by {@link PactArrayMode#ARRAY_CONTAINS}.
     *
     * @return the alternative model type names
     */
    String[] containsTypes() default {};
}
