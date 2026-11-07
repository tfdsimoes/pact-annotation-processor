package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ScalarBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.stringType("root-value");
  }

  public fixture.RootBodies.Scalar buildExpectedInstance() {
    return createExpected_fixture_RootBodies_Scalar();
  }

  private static fixture.RootBodies.Scalar createExpected_fixture_RootBodies_Scalar() {
    fixture.RootBodies.Scalar result = new fixture.RootBodies.Scalar();
    result.value = "root-value";
    result.ignored = "not-root";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
