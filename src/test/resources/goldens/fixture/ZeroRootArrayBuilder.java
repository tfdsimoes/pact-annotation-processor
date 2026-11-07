package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ZeroRootArrayBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMaxUnordered(0, array -> {
    }).build();
  }

  public fixture.ZeroRootArray buildExpectedInstance() {
    return createExpected_fixture_ZeroRootArray();
  }

  private static fixture.ZeroRootArray createExpected_fixture_ZeroRootArray() {
    fixture.ZeroRootArray result = new fixture.ZeroRootArray();
    result.values = java.util.Arrays.asList();
    return result;
  }

  private static fixture.ZeroRootArray.Item createExpected_fixture_ZeroRootArray_Item() {
    fixture.ZeroRootArray.Item result = new fixture.ZeroRootArray.Item();
    result.name = "example";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
