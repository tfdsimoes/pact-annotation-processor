package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class AutoBooleanBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.booleanType(true);
  }

  public fixture.RootValueMatchers.AutoBoolean buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_AutoBoolean();
  }

  private static fixture.RootValueMatchers.AutoBoolean createExpected_fixture_RootValueMatchers_AutoBoolean() {
    fixture.RootValueMatchers.AutoBoolean result = new fixture.RootValueMatchers.AutoBoolean();
    result.value = true;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
