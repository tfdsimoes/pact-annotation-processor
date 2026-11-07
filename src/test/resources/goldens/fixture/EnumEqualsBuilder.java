package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class EnumEqualsBuilder {
  public DslPart build() {
    return exactValue("DONE");
  }

  public fixture.RootValueMatchers.EnumEquals buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_EnumEquals();
  }

  private static fixture.RootValueMatchers.EnumEquals createExpected_fixture_RootValueMatchers_EnumEquals() {
    fixture.RootValueMatchers.EnumEquals result = new fixture.RootValueMatchers.EnumEquals();
    result.value = fixture.RootValueMatchers.State.DONE;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
