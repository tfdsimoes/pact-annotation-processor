package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class IntegerTypeBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.integerType((long) 42L);
  }

  public fixture.RootValueMatchers.IntegerType buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_IntegerType();
  }

  private static fixture.RootValueMatchers.IntegerType createExpected_fixture_RootValueMatchers_IntegerType() {
    fixture.RootValueMatchers.IntegerType result = new fixture.RootValueMatchers.IntegerType();
    result.value = 42L;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
