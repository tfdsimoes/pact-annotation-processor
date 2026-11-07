package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class PrivateSetterModelBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.stringType("value", "configured");
    }).build();
  }

  public fixture.PrivateSetterModel buildExpectedInstance() {
    return createExpected_fixture_PrivateSetterModel();
  }

  private static fixture.PrivateSetterModel createExpected_fixture_PrivateSetterModel() {
    fixture.PrivateSetterModel result = new fixture.PrivateSetterModel();
    result.setValue("configured");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
