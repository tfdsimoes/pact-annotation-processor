package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class TimeBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.time("HH:mm:ss", java.util.Date.from(java.time.LocalTime.parse("12:30:45", java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale.ROOT)).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneId.systemDefault()).toInstant()));
  }

  public fixture.RootValueMatchers.Time buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_Time();
  }

  private static fixture.RootValueMatchers.Time createExpected_fixture_RootValueMatchers_Time() {
    fixture.RootValueMatchers.Time result = new fixture.RootValueMatchers.Time();
    result.value = java.time.LocalTime.parse("12:30:45", java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale.ROOT));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
