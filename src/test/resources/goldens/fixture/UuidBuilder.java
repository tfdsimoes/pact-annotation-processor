package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class UuidBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.uuid(java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"));
  }

  public fixture.RootValueMatchers.Uuid buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_Uuid();
  }

  private static fixture.RootValueMatchers.Uuid createExpected_fixture_RootValueMatchers_Uuid() {
    fixture.RootValueMatchers.Uuid result = new fixture.RootValueMatchers.Uuid();
    result.value = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
