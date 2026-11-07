package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class ArrayObjectsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonArray(array -> {
      array.object(item -> {
        item.stringType("name", "example");
      });
    }).build();
  }

  public fixture.RootArrayKinds.ArrayObjects buildExpectedInstance() {
    return createExpected_fixture_RootArrayKinds_ArrayObjects();
  }

  private static fixture.RootArrayKinds.ArrayObjects createExpected_fixture_RootArrayKinds_ArrayObjects() {
    fixture.RootArrayKinds.ArrayObjects result = new fixture.RootArrayKinds.ArrayObjects();
    result.values = java.util.Arrays.asList(createExpected_fixture_RootArrayKinds_Item());
    return result;
  }

  private static fixture.RootArrayKinds.Item createExpected_fixture_RootArrayKinds_Item() {
    fixture.RootArrayKinds.Item result = new fixture.RootArrayKinds.Item();
    result.name = "example";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
