package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ZeroArrayBoundsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.maxArrayLike("emptyStrings", 0, PactDslJsonRootValue.stringType("example"), 0);
      body.maxArrayLike("emptyNumbers", 0, PactDslJsonRootValue.integerType((long) 1L), 0);
      body.maxArrayLike("emptyObjects", 0, 0, emptyArrayItem3 -> {});
      body.maxArrayLike("emptyNestedArrays", 0, 0, emptyArrayItem3 -> {});
      body.maxArrayLike("emptySet", 0, PactDslJsonRootValue.stringType("example"), 0);
      body.maxArrayLike("emptyUnordered", 0, PactDslJsonRootValue.booleanType(true), 0);
    }).build();
  }

  public fixture.ZeroArrayBounds buildExpectedInstance() {
    return createExpected_fixture_ZeroArrayBounds();
  }

  private static fixture.ZeroArrayBounds createExpected_fixture_ZeroArrayBounds() {
    fixture.ZeroArrayBounds result = new fixture.ZeroArrayBounds();
    result.emptyStrings = java.util.Arrays.asList();
    result.emptyNumbers = java.util.Arrays.asList();
    result.emptyObjects = java.util.Arrays.asList();
    result.emptyNestedArrays = new int[][]{};
    result.emptySet = new java.util.LinkedHashSet<>(java.util.Arrays.asList());
    result.emptyUnordered = java.util.Arrays.asList();
    return result;
  }

  private static fixture.ZeroArrayBounds.Address createExpected_fixture_ZeroArrayBounds_Address() {
    fixture.ZeroArrayBounds.Address result = new fixture.ZeroArrayBounds.Address();
    result.city = "example";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
