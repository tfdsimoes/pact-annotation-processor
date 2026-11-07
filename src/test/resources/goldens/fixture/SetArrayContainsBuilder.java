package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class SetArrayContainsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.arrayContaining("colors", items -> {
        items.stringValue("red");
        items.stringValue("blue");
      });
    }).build();
  }

  public fixture.SetArrayContains buildExpectedInstance() {
    return createExpected_fixture_SetArrayContains();
  }

  private static fixture.SetArrayContains createExpected_fixture_SetArrayContains() {
    fixture.SetArrayContains result = new fixture.SetArrayContains();
    result.colors = new java.util.LinkedHashSet<>(java.util.Arrays.asList("red", "blue"));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
