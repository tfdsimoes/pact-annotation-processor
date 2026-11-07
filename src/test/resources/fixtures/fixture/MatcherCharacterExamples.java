package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;

@PactDslBodyBuilder
public class MatcherCharacterExamples {
    @PactMatcher(value = PactMatcherKind.REGEX, regex = "x")
    public char regexCharacter;

    @PactMatcher(PactMatcherKind.HEX)
    public char hexadecimalCharacter;

    @PactMatcher(value = PactMatcherKind.INCLUDES, example = "x")
    public char includedCharacter;
}
