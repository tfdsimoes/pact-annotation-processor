package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayNestedBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.eachArrayLike(inner -> {
        inner.integerType((long) 1L);
      });
    }).build();
  }

  public fixture.RootArrayKinds.ArrayNested buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayNested();
  }

  private static fixture.RootArrayKinds.ArrayNested createExpected_fixture_RootArrayKinds_ArrayNested() {
    fixture.RootArrayKinds.ArrayNested result = new fixture.RootArrayKinds.ArrayNested();
    result.values = java.util.Arrays.asList(java.util.Arrays.asList(1));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
