package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayUnorderedMinMaxBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMinMaxUnordered(1, 3, array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayUnorderedMinMax buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayUnorderedMinMax();
  }

  private static fixture.RootArrayModes.ArrayUnorderedMinMax createExpected_fixture_RootArrayModes_ArrayUnorderedMinMax() {
    fixture.RootArrayModes.ArrayUnorderedMinMax result = new fixture.RootArrayModes.ArrayUnorderedMinMax();
    result.values = java.util.Arrays.asList("example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
