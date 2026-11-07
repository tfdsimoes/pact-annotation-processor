package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ConstraintModelsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.minArrayLike("minimum", 2, PactDslJsonRootValue.stringType("example"), 2);
      body.minMaxArrayLike("bounded", 3, 5, PactDslJsonRootValue.integerType((long) 1L), 3);
      body.maxArrayLike("maximum", 4, PactDslJsonRootValue.stringType("example"), 1);
      body.minArrayLike("arrayMinimum", 2, PactDslJsonRootValue.stringType("example"), 2);
    }).build();
  }

  public fixture.ConstraintModels buildExpectedInstance() {
    return createExpected_fixture_ConstraintModels();
  }

  private static fixture.ConstraintModels createExpected_fixture_ConstraintModels() {
    fixture.ConstraintModels result = new fixture.ConstraintModels();
    result.minimum = java.util.Arrays.asList("example", "example");
    result.bounded = java.util.Arrays.asList(1, 1, 1);
    result.maximum = java.util.Arrays.asList("example");
    result.arrayMinimum = new java.lang.String[]{"example", "example"};
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
