package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayUuidsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.uuid("00000000-0000-0000-0000-000000000001");
    }).build();
  }

  public fixture.RootArrayKinds.ArrayUuids buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayUuids();
  }

  private static fixture.RootArrayKinds.ArrayUuids createExpected_fixture_RootArrayKinds_ArrayUuids() {
    fixture.RootArrayKinds.ArrayUuids result = new fixture.RootArrayKinds.ArrayUuids();
    result.values = java.util.Arrays.asList(java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
