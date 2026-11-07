package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayMinBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMinLike(2, array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayMin buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayMin();
  }

  private static fixture.RootArrayModes.ArrayMin createExpected_fixture_RootArrayModes_ArrayMin() {
    fixture.RootArrayModes.ArrayMin result = new fixture.RootArrayModes.ArrayMin();
    result.values = java.util.Arrays.asList("example", "example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
