package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class TemporalValuesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.date("date", "dd/MM/yyyy", java.time.LocalDate.parse("02/01/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT)));
      body.time("time", "hh:mm a", java.util.Date.from(java.time.LocalTime.parse("03:04 PM", java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ROOT)).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneOffset.UTC).toInstant()), java.util.TimeZone.getTimeZone("UTC"));
      body.datetime("datetime", "dd-MM-yyyy HH:mm:ss", java.util.Date.from(java.time.LocalDateTime.parse("02-01-2024 03:04:05", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", java.util.Locale.ROOT)).atZone(java.time.ZoneOffset.UTC).toInstant()), java.util.TimeZone.getTimeZone("UTC"));
      body.datetime("instant", "dd/MM/yyyy HH:mm:ss XXX", java.util.Date.from(java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss XXX", java.util.Locale.ROOT).parse("02/01/2024 03:04:05 +00:00"))), java.util.TimeZone.getTimeZone("UTC"));
    }).build();
  }

  public fixture.TemporalValues buildExpectedInstance() {
    return createExpected_fixture_TemporalValues();
  }

  private static fixture.TemporalValues createExpected_fixture_TemporalValues() {
    fixture.TemporalValues result = new fixture.TemporalValues();
    result.date = java.time.LocalDate.parse("02/01/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT));
    result.time = java.time.LocalTime.parse("03:04 PM", java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ROOT));
    result.datetime = java.time.LocalDateTime.parse("02-01-2024 03:04:05", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", java.util.Locale.ROOT));
    result.instant = java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss XXX", java.util.Locale.ROOT).parse("02/01/2024 03:04:05 +00:00"));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
