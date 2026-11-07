package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class NumericMatcherParityBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.numberType("numberFloat", 0.1);
      body.numberType("suffixedFloat", 5.0);
      body.decimalType("decimalValue", new java.math.BigDecimal("0.1").doubleValue());
      body.decimalType("exactInteger", new java.math.BigInteger("9007199254740992").doubleValue());
      body.id("minimumId", (long) -9223372036854775808L);
      body.id("maximumId", (long) 9223372036854775807L);
    }).build();
  }

  public fixture.NumericMatcherParity buildExpectedInstance() {
    return createExpected_fixture_NumericMatcherParity();
  }

  private static fixture.NumericMatcherParity createExpected_fixture_NumericMatcherParity() {
    fixture.NumericMatcherParity result = new fixture.NumericMatcherParity();
    result.numberFloat = 0.1F;
    result.suffixedFloat = 5.0F;
    result.decimalValue = new java.math.BigDecimal("0.1");
    result.exactInteger = new java.math.BigInteger("9007199254740992");
    result.minimumId = -9223372036854775808L;
    result.maximumId = 9223372036854775807L;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
