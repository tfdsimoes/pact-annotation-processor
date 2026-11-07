package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class NumberTypeBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.numberType(new java.math.BigDecimal("123.45"));
  }

  public fixture.RootValueMatchers.NumberType buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_NumberType();
  }

  private static fixture.RootValueMatchers.NumberType createExpected_fixture_RootValueMatchers_NumberType() {
    fixture.RootValueMatchers.NumberType result = new fixture.RootValueMatchers.NumberType();
    result.value = new java.math.BigDecimal("123.45");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
