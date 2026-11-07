package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ServiceLoadedBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.stringType("value", "example");
    }).build();
  }

  public fixture.ServiceLoaded buildExpectedInstance() {
    return createExpected_fixture_ServiceLoaded();
  }

  private static fixture.ServiceLoaded createExpected_fixture_ServiceLoaded() {
    fixture.ServiceLoaded result = new fixture.ServiceLoaded();
    result.value = "example";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
