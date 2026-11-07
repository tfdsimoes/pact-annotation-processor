package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayModesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.minArrayLike("minimum", 2, PactDslJsonRootValue.stringType("example"), 2);
      body.maxArrayLike("maximum", 4, PactDslJsonRootValue.integerType((long) 1L), 1);
      body.minMaxArrayLike("bounded", 1, 3, PactDslJsonRootValue.booleanType(true), 1);
      body.unorderedArray("unordered", array -> array.eachLike(PactDslJsonRootValue.stringType("example")));
      body.unorderedMinArray("unorderedMinimum", 2, array -> array.eachLike(PactDslJsonRootValue.stringType("example")));
      body.unorderedMaxArray("unorderedMaximum", 4, array -> array.eachLike(PactDslJsonRootValue.stringType("example")));
      body.unorderedMinMaxArray("unorderedBounded", 1, 3, array -> array.eachLike(PactDslJsonRootValue.stringType("example")));
      body.unorderedMinArray("nested", 2, item3 -> {
        item3.array(inner -> {
          inner.integerType((long) 1L);
        });
      });
    }).build();
  }

  public fixture.ArrayModes buildExpectedInstance() {
    return createExpected_fixture_ArrayModes();
  }

  private static fixture.ArrayModes createExpected_fixture_ArrayModes() {
    fixture.ArrayModes result = new fixture.ArrayModes();
    result.minimum = java.util.Arrays.asList("example", "example");
    result.maximum = java.util.Arrays.asList(1);
    result.bounded = java.util.Arrays.asList(true);
    result.unordered = java.util.Arrays.asList("example");
    result.unorderedMinimum = java.util.Arrays.asList("example", "example");
    result.unorderedMaximum = java.util.Arrays.asList("example");
    result.unorderedBounded = java.util.Arrays.asList("example");
    result.nested = new int[][]{new int[]{1, 1}, new int[]{1, 1}};
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
