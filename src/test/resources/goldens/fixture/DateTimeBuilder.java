package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class DateTimeBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.datetime("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Date.from(java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT).parse("2024-01-02T03:04:05Z"))));
  }

  public fixture.RootValueMatchers.DateTime buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_DateTime();
  }

  private static fixture.RootValueMatchers.DateTime createExpected_fixture_RootValueMatchers_DateTime() {
    fixture.RootValueMatchers.DateTime result = new fixture.RootValueMatchers.DateTime();
    result.value = java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT).parse("2024-01-02T03:04:05Z"));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
