package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayUnorderedMinBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMinUnordered(2, array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayUnorderedMin buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayUnorderedMin();
  }

  private static fixture.RootArrayModes.ArrayUnorderedMin createExpected_fixture_RootArrayModes_ArrayUnorderedMin() {
    fixture.RootArrayModes.ArrayUnorderedMin result = new fixture.RootArrayModes.ArrayUnorderedMin();
    result.values = java.util.Arrays.asList("example", "example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
