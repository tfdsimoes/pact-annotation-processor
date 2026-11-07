package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class WideRootNumberBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.numberType(new java.math.BigInteger("12345678901234567890"));
  }

  public fixture.WideRootNumber buildExpectedInstance() {
    return createExpected_fixture_WideRootNumber();
  }

  private static fixture.WideRootNumber createExpected_fixture_WideRootNumber() {
    fixture.WideRootNumber result = new fixture.WideRootNumber();
    result.value = new java.math.BigInteger("12345678901234567890");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
