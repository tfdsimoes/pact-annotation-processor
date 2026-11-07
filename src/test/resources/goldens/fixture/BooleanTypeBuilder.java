package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class BooleanTypeBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.booleanType(false);
  }

  public fixture.RootValueMatchers.BooleanType buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_BooleanType();
  }

  private static fixture.RootValueMatchers.BooleanType createExpected_fixture_RootValueMatchers_BooleanType() {
    fixture.RootValueMatchers.BooleanType result = new fixture.RootValueMatchers.BooleanType();
    result.value = false;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
