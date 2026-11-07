package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayMaxBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMaxLike(3, array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayMax buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayMax();
  }

  private static fixture.RootArrayModes.ArrayMax createExpected_fixture_RootArrayModes_ArrayMax() {
    fixture.RootArrayModes.ArrayMax result = new fixture.RootArrayModes.ArrayMax();
    result.values = java.util.Arrays.asList("example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
