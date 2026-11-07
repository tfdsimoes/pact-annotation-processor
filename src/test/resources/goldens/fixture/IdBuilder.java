package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class IdBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.id((long) 42L);
  }

  public fixture.RootValueMatchers.Id buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_Id();
  }

  private static fixture.RootValueMatchers.Id createExpected_fixture_RootValueMatchers_Id() {
    fixture.RootValueMatchers.Id result = new fixture.RootValueMatchers.Id();
    result.value = 42L;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
