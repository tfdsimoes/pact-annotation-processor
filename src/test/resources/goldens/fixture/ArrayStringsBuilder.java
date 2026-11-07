package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayStringsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.stringType("example");
    }).build();
  }

  public fixture.RootArrayKinds.ArrayStrings buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayStrings();
  }

  private static fixture.RootArrayKinds.ArrayStrings createExpected_fixture_RootArrayKinds_ArrayStrings() {
    fixture.RootArrayKinds.ArrayStrings result = new fixture.RootArrayKinds.ArrayStrings();
    result.values = java.util.Arrays.asList("example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
