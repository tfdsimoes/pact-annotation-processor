package com.pact.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source-retained field annotation excluding a model property from the Pact body and generated
 * expected instance.
 *
 * <p>Excluded fields are not validated by the processor.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PactExclude {
}
