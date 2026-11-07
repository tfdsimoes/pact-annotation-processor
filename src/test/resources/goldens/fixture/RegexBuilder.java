package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class RegexBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.stringMatcher("[A-Z]+", "READY");
  }

  public fixture.RootValueMatchers.Regex buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_Regex();
  }

  private static fixture.RootValueMatchers.Regex createExpected_fixture_RootValueMatchers_Regex() {
    fixture.RootValueMatchers.Regex result = new fixture.RootValueMatchers.Regex();
    result.value = "READY";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
