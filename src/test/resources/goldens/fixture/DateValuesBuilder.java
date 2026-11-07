package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class DateValuesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.date("date", "dd/MM/yyyy", java.util.Date.from(java.time.LocalDate.from(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT).parse("02/01/2024")).atStartOfDay(java.time.ZoneOffset.UTC).toInstant()), java.util.TimeZone.getTimeZone("UTC"));
      body.time("time", "hh:mm a", java.util.Date.from(java.time.LocalTime.from(java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ROOT).parse("03:04 PM")).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneOffset.UTC).toInstant()), java.util.TimeZone.getTimeZone("UTC"));
      body.datetime("datetime", "dd/MM/yyyy HH:mm:ss XXX", java.util.Date.from(java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss XXX", java.util.Locale.ROOT).parse("02/01/2024 03:04:05 +00:00"))), java.util.TimeZone.getTimeZone("UTC"));
    }).build();
  }

  public fixture.DateValues buildExpectedInstance() {
    return createExpected_fixture_DateValues();
  }

  private static fixture.DateValues createExpected_fixture_DateValues() {
    fixture.DateValues result = new fixture.DateValues();
    result.date = java.util.Date.from(java.time.LocalDate.from(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT).parse("02/01/2024")).atStartOfDay(java.time.ZoneOffset.UTC).toInstant());
    result.time = java.util.Date.from(java.time.LocalTime.from(java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ROOT).parse("03:04 PM")).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneOffset.UTC).toInstant());
    result.datetime = java.util.Date.from(java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss XXX", java.util.Locale.ROOT).parse("02/01/2024 03:04:05 +00:00")));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
