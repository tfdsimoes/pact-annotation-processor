package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class StringEscapesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.stringType("text", "quote: \" slash: \\ newline:\n tab:\t");
      body.stringType("backspace", "\b");
      body.stringType("formFeed", "\f");
      body.stringType("control", "\001");
      body.stringType("quote", "'");
      body.stringType("backslash", "\\");
      body.stringType("newline", "\n");
    }).build();
  }

  public fixture.StringEscapes buildExpectedInstance() {
    return createExpected_fixture_StringEscapes();
  }

  private static fixture.StringEscapes createExpected_fixture_StringEscapes() {
    fixture.StringEscapes result = new fixture.StringEscapes();
    result.text = "quote: \" slash: \\ newline:\n tab:\t";
    result.backspace = '\b';
    result.formFeed = '\f';
    result.control = '\001';
    result.quote = '\'';
    result.backslash = '\\';
    result.newline = '\n';
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
