package example;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class AdvancedOrderBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.eachValueLike("sku-1", PactDslJsonRootValue.integerType((long) 1L));
      body.date("created", "dd/MM/yyyy", java.time.LocalDate.parse("02/01/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT)));
      body.integerType("adjustment", (int) (long) 1);
      body.minArrayLike("tags", 2, PactDslJsonRootValue.stringType("example"), 2);
    }).build();
  }

  public example.AdvancedOrder buildExpectedInstance() {
    return createExpected_example_AdvancedOrder();
  }

  private static example.AdvancedOrder createExpected_example_AdvancedOrder() {
    example.AdvancedOrder result = new example.AdvancedOrder();
    result.quantities = java.util.Collections.singletonMap("sku-1", 1);
    result.created = java.time.LocalDate.parse("02/01/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT));
    result.adjustment = 1;
    result.tags = java.util.Arrays.asList("example", "example");
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
