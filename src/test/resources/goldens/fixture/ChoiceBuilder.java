package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ChoiceBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.arrayContaining("colors", items -> {
        items.stringValue("red");
        items.stringValue("blue");
      });
      body.arrayContaining("quantities", items -> {
        items.numberValue(2);
        items.numberValue(7);
      });
      body.arrayContaining("flags", items -> {
        items.booleanValue(true);
        items.booleanValue(false);
      });
      body.arrayContaining("statuses", items -> {
        items.stringValue("READY");
        items.stringValue("DONE");
      });
    }).build();
  }

  public fixture.Choice buildExpectedInstance() {
    return createExpected_fixture_Choice();
  }

  private static fixture.Choice createExpected_fixture_Choice() {
    fixture.Choice result = new fixture.Choice();
    result.colors = java.util.Arrays.asList("red", "blue");
    result.quantities = java.util.Arrays.asList(2, 7);
    result.flags = java.util.Arrays.asList(true, false);
    result.statuses = java.util.Arrays.asList(fixture.Choice.Status.READY, fixture.Choice.Status.DONE);
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
