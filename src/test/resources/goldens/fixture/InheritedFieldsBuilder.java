package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class InheritedFieldsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.stringType("inherited", "example");
      body.stringType("child", "example");
    }).build();
  }

  public fixture.InheritedFields buildExpectedInstance() {
    return createExpected_fixture_InheritedFields();
  }

  private static fixture.InheritedFields createExpected_fixture_InheritedFields() {
    fixture.InheritedFields result = new fixture.InheritedFields();
    result.inherited = "example";
    result.child = "example";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
