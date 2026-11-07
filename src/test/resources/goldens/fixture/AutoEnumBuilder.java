package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class AutoEnumBuilder {
  public DslPart build() {
    return exactValue("READY");
  }

  public fixture.RootValueMatchers.AutoEnum buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_AutoEnum();
  }

  private static fixture.RootValueMatchers.AutoEnum createExpected_fixture_RootValueMatchers_AutoEnum() {
    fixture.RootValueMatchers.AutoEnum result = new fixture.RootValueMatchers.AutoEnum();
    result.value = fixture.RootValueMatchers.State.READY;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
