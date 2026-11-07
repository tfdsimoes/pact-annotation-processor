package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayEnumsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.stringValue("READY");
    }).build();
  }

  public fixture.RootArrayKinds.ArrayEnums buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayEnums();
  }

  private static fixture.RootArrayKinds.ArrayEnums createExpected_fixture_RootArrayKinds_ArrayEnums() {
    fixture.RootArrayKinds.ArrayEnums result = new fixture.RootArrayKinds.ArrayEnums();
    result.values = java.util.Arrays.asList(fixture.RootArrayKinds.State.READY);
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
