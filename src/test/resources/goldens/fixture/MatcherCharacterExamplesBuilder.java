package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class MatcherCharacterExamplesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.stringMatcher("regexCharacter", "x", "x");
      body.getPactDslObject().hexValue("hexadecimalCharacter", "A");
      body.getPactDslObject().includesStr("includedCharacter", "x");
    }).build();
  }

  public fixture.MatcherCharacterExamples buildExpectedInstance() {
    return createExpected_fixture_MatcherCharacterExamples();
  }

  private static fixture.MatcherCharacterExamples createExpected_fixture_MatcherCharacterExamples() {
    fixture.MatcherCharacterExamples result = new fixture.MatcherCharacterExamples();
    result.regexCharacter = 'x';
    result.hexadecimalCharacter = 'A';
    result.includedCharacter = 'x';
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
