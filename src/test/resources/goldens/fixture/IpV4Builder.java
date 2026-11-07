package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class IpV4Builder {
  public DslPart build() {
    return PactDslJsonRootValue.ipAddress();
  }

  public fixture.RootValueMatchers.IpV4 buildExpectedInstance() {
    return createExpected_fixture_RootValueMatchers_IpV4();
  }

  private static fixture.RootValueMatchers.IpV4 createExpected_fixture_RootValueMatchers_IpV4() {
    fixture.RootValueMatchers.IpV4 result = new fixture.RootValueMatchers.IpV4();
    result.value = "127.0.0.1";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
