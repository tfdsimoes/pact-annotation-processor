package example;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ScalarBodyBuilder {
  public DslPart build() {
    return PactDslJsonRootValue.stringType("accepted");
  }

  public example.ScalarBody buildExpectedInstance() {
    return createExpected_example_ScalarBody();
  }

  private static example.ScalarBody createExpected_example_ScalarBody() {
    example.ScalarBody result = new example.ScalarBody();
    result.value = "accepted";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
