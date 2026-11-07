package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayUnorderedMaxBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMaxUnordered(3, array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayUnorderedMax buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayUnorderedMax();
  }

  private static fixture.RootArrayModes.ArrayUnorderedMax createExpected_fixture_RootArrayModes_ArrayUnorderedMax() {
    fixture.RootArrayModes.ArrayUnorderedMax result = new fixture.RootArrayModes.ArrayUnorderedMax();
    result.values = java.util.Arrays.asList("example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
