package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class RootTemporalValueBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.time("hh:mm a", java.util.Date.from(java.time.LocalTime.parse("03:04 PM", java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ROOT)).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneId.systemDefault()).toInstant()));
  }

  public fixture.RootTemporalValue buildExpectedInstance() {
    return createExpected_fixture_RootTemporalValue();
  }

  private static fixture.RootTemporalValue createExpected_fixture_RootTemporalValue() {
    fixture.RootTemporalValue result = new fixture.RootTemporalValue();
    result.value = java.time.LocalTime.parse("03:04 PM", java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ROOT));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
