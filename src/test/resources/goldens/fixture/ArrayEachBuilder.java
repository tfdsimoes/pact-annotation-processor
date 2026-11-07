package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayEachBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayEach buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayEach();
  }

  private static fixture.RootArrayModes.ArrayEach createExpected_fixture_RootArrayModes_ArrayEach() {
    fixture.RootArrayModes.ArrayEach result = new fixture.RootArrayModes.ArrayEach();
    result.values = java.util.Arrays.asList("example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
