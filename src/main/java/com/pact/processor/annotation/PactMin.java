package com.pact.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-retained field annotation setting the minimum numeric threshold for the example selected
 * for a supported numeric property.
 *
 * <p>The threshold is a {@code long}; this annotation does not define a Pact numeric range matcher
 * and cannot express fractional thresholds, even when the field uses a decimal type.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PactMin {
    /**
     * @return the inclusive minimum numeric threshold
     */
    long value();
}
