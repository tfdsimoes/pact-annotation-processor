package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class StringTypeBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.stringType("sample");
  }

  public fixture.RootValueMatchers.StringType buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_StringType();
  }

  private static fixture.RootValueMatchers.StringType createExpected_fixture_RootValueMatchers_StringType() {
    fixture.RootValueMatchers.StringType result = new fixture.RootValueMatchers.StringType();
    result.value = "sample";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
