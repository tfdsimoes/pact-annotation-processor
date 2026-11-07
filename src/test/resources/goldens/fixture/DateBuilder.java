package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class DateBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.date("dd/MM/yyyy", java.util.Date.from(java.time.LocalDate.parse("31/12/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT)).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
  }

  public fixture.RootValueMatchers.Date buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_Date();
  }

  private static fixture.RootValueMatchers.Date createExpected_fixture_RootValueMatchers_Date() {
    fixture.RootValueMatchers.Date result = new fixture.RootValueMatchers.Date();
    result.value = java.time.LocalDate.parse("31/12/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
