package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayUnorderedBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayUnordered(array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayUnordered buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayUnordered();
  }

  private static fixture.RootArrayModes.ArrayUnordered createExpected_fixture_RootArrayModes_ArrayUnordered() {
    fixture.RootArrayModes.ArrayUnordered result = new fixture.RootArrayModes.ArrayUnordered();
    result.values = java.util.Arrays.asList("example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
