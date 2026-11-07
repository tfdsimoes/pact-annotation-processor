package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMinLike(2, array -> {
      array.integerType((long) 1L);
    }).build();
  }

  public fixture.RootBodies.Array buildExpectedInstance() {
    return createExpected_fixture_RootBodies_Array();
  }

  private static fixture.RootBodies.Array createExpected_fixture_RootBodies_Array() {
    fixture.RootBodies.Array result = new fixture.RootBodies.Array();
    result.values = java.util.Arrays.asList(1, 1);
    result.ignored = "not-root";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
