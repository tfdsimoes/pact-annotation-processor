package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class HexBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.hexValue("AB12");
  }

  public fixture.RootValueMatchers.Hex buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_Hex();
  }

  private static fixture.RootValueMatchers.Hex createExpected_fixture_RootValueMatchers_Hex() {
    fixture.RootValueMatchers.Hex result = new fixture.RootValueMatchers.Hex();
    result.value = "AB12";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
