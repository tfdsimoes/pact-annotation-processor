package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayDatesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.stringValue("2024-01-02");
    }).build();
  }

  public fixture.RootArrayKinds.ArrayDates buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayDates();
  }

  private static fixture.RootArrayKinds.ArrayDates createExpected_fixture_RootArrayKinds_ArrayDates() {
    fixture.RootArrayKinds.ArrayDates result = new fixture.RootArrayKinds.ArrayDates();
    result.values = java.util.Arrays.asList(java.time.LocalDate.parse("2024-01-02", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd", java.util.Locale.ROOT)));
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
