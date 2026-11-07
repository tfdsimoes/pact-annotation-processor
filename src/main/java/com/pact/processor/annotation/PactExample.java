package com.pact.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-retained field annotation supplying an explicit example for a supported model property.
 *
 * <p>For temporal fields, {@link #format()} overrides the type-specific default pattern. If both
 * this annotation and {@link PactMatcher#example()} supply an example, their values must agree;
 * conflicting values are reported as compile-time errors.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PactExample {
    /**
     * The example value, parsed and validated at compile time.
     *
     * @return the example text
     */
    String value();

    /**
     * Optional date/time pattern; applies only to supported temporal fields.
     *
     * @return the temporal pattern, or an empty string to use the default
     */
    String format() default "";
}
