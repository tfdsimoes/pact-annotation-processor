package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class NullValueBuilder {
  public DslPart build() {
    return exactValue(null);
  }

  public fixture.RootValueMatchers.NullValue buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_NullValue();
  }

  private static fixture.RootValueMatchers.NullValue createExpected_fixture_RootValueMatchers_NullValue() {
    fixture.RootValueMatchers.NullValue result = new fixture.RootValueMatchers.NullValue();
    result.value = null;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
