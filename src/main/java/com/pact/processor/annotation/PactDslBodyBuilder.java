package com.pact.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-retained type annotation marking a model for compile-time generation of a Pact JSON body
 * builder and expected-instance builder.
 *
 * <p>Object roots use all supported non-excluded fields. Value and array roots use the field named by
 * {@link #rootField()}, or require exactly one non-excluded field when it is omitted.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface PactDslBodyBuilder {
    /**
     * Selects whether the generated body is an object, array, or scalar value.
     *
     * @return the root body shape
     */
    PactRootType root() default PactRootType.OBJECT;

    /**
     * Selects the field used by a non-object root; ignored only when empty.
     *
     * @return the selected root field name, or an empty string for automatic selection
     */
    String rootField() default "";
}
