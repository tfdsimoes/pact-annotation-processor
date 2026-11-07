package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayDecimalsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.decimalType(new java.math.BigDecimal("1.5").doubleValue());
    }).build();
  }

  public fixture.RootArrayKinds.ArrayDecimals buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayDecimals();
  }

  private static fixture.RootArrayKinds.ArrayDecimals createExpected_fixture_RootArrayKinds_ArrayDecimals() {
    fixture.RootArrayKinds.ArrayDecimals result = new fixture.RootArrayKinds.ArrayDecimals();
    result.values = java.util.Arrays.asList(new java.math.BigDecimal("1.5"));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
