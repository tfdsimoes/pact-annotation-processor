package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayMinMaxBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMinMaxLike(1, 3, array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayModes.ArrayMinMax buildExpectedInstance() {
    return createExpected_fixture_RootArrayModes_ArrayMinMax();
  }

  private static fixture.RootArrayModes.ArrayMinMax createExpected_fixture_RootArrayModes_ArrayMinMax() {
    fixture.RootArrayModes.ArrayMinMax result = new fixture.RootArrayModes.ArrayMinMax();
    result.values = java.util.Arrays.asList("example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
