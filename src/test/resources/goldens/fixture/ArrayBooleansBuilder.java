package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayBooleansBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.booleanType(true);
    }).build();
  }

  public fixture.RootArrayKinds.ArrayBooleans buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayBooleans();
  }

  private static fixture.RootArrayKinds.ArrayBooleans createExpected_fixture_RootArrayKinds_ArrayBooleans() {
    fixture.RootArrayKinds.ArrayBooleans result = new fixture.RootArrayKinds.ArrayBooleans();
    result.values = java.util.Arrays.asList(true);
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
