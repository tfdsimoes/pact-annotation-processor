package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class TemporalDefaultsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.datetime("localDateTime", "yyyy-MM-dd'T'HH:mm:ss", java.util.Date.from(java.time.LocalDateTime.parse("2024-01-02T03:04:05", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.ROOT)).atZone(java.time.ZoneOffset.UTC).toInstant()), java.util.TimeZone.getTimeZone("UTC"));
      body.time("localTime", "HH:mm:ss", java.util.Date.from(java.time.LocalTime.parse("03:04:05", java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale.ROOT)).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneOffset.UTC).toInstant()), java.util.TimeZone.getTimeZone("UTC"));
      body.datetime("zonedDateTime", "yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Date.from(java.time.ZonedDateTime.parse("2024-01-02T03:04:05Z", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT)).toInstant()), java.util.TimeZone.getTimeZone(java.time.ZonedDateTime.parse("2024-01-02T03:04:05Z", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT)).getZone()));
      body.datetime("offsetDateTime", "yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Date.from(java.time.OffsetDateTime.parse("2024-01-02T03:04:05+02:00", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT)).toInstant()), java.util.TimeZone.getTimeZone("GMT" + java.time.OffsetDateTime.parse("2024-01-02T03:04:05+02:00", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT)).getOffset().getId()));
    }).build();
  }

  public fixture.TemporalDefaults buildExpectedInstance() {
    return createExpected_fixture_TemporalDefaults();
  }

  private static fixture.TemporalDefaults createExpected_fixture_TemporalDefaults() {
    fixture.TemporalDefaults result = new fixture.TemporalDefaults();
    result.localDateTime = java.time.LocalDateTime.parse("2024-01-02T03:04:05", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.ROOT));
    result.localTime = java.time.LocalTime.parse("03:04:05", java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale.ROOT));
    result.zonedDateTime = java.time.ZonedDateTime.parse("2024-01-02T03:04:05Z", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT));
    result.offsetDateTime = java.time.OffsetDateTime.parse("2024-01-02T03:04:05+02:00", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
