package com.pact.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-retained field annotation configuring the representative key for a supported
 * {@code Map<String, V>} property.
 *
 * <p>A map field must declare this annotation; the selected key and its associated value are
 * generated as the map example.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PactMapMatcher {
    /**
     * Representative map key; defaults to {@code "key"}.
     *
     * @return the representative key
     */
    String keyExample() default "key";
}
