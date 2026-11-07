package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class DecimalTypeBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.decimalType((double) 1.25);
  }

  public fixture.RootValueMatchers.DecimalType buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_DecimalType();
  }

  private static fixture.RootValueMatchers.DecimalType createExpected_fixture_RootValueMatchers_DecimalType() {
    fixture.RootValueMatchers.DecimalType result = new fixture.RootValueMatchers.DecimalType();
    result.value = 1.25D;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
