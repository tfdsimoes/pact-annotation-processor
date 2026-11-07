package example;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayBodyBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArrayMinUnordered(2, array -> {
      array.object(item -> {
        item.stringType("id", "ORD-42");
        item.stringMatcher("reference", "[A-Z]{3}-[0-9]+", "ORD-42");
        item.minMaxArrayLike("tags", 1, 5, PactDslJsonRootValue.stringType("example"), 1);
        item.object("shippingAddress", nested4 -> {
          nested4.stringType("city", "example");
          nested4.integerType("postalCode", (int) (long) 1);
        });
      });
    }).build();
  }

  public example.ArrayBody buildExpectedInstance() {
    return createExpected_example_ArrayBody();
  }

  private static example.ArrayBody createExpected_example_ArrayBody() {
    example.ArrayBody result = new example.ArrayBody();
    result.orders = java.util.Arrays.asList(createExpected_example_Order(), createExpected_example_Order());
    return result;
  }

  private static example.Order createExpected_example_Order() {
    example.Order result = new example.Order();
    result.id = "ORD-42";
    result.reference = "ORD-42";
    result.tags = java.util.Arrays.asList("example");
    result.shippingAddress = createExpected_example_Order_Address();
    return result;
  }

  private static example.Order.Address createExpected_example_Order_Address() {
    example.Order.Address result = new example.Order.Address();
    result.city = "example";
    result.postalCode = 1;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
