package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class NumericBoundariesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.integerType("negativeRange", (int) (long) -2);
      body.integerType("maxMinusOne", (int) (long) -1);
      body.integerType("positiveRange", (int) (long) 5);
      body.minArrayLike("values", 3, PactDslJsonRootValue.integerType((long) 1L), 3);
      body.integerType("minimumInteger", (int) (long) -2147483648);
      body.integerType("maximumInteger", (int) (long) 2147483647);
    }).build();
  }

  public fixture.NumericBoundaries buildExpectedInstance() {
    return createExpected_fixture_NumericBoundaries();
  }

  private static fixture.NumericBoundaries createExpected_fixture_NumericBoundaries() {
    fixture.NumericBoundaries result = new fixture.NumericBoundaries();
    result.negativeRange = -2;
    result.maxMinusOne = -1;
    result.positiveRange = 5;
    result.values = java.util.Arrays.asList(1, 1, 1);
    result.minimumInteger = -2147483648;
    result.maximumInteger = 2147483647;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
