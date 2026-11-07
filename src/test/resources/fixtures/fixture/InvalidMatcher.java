package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;

@PactDslBodyBuilder
public class InvalidMatcher {
    @PactMatcher(PactMatcherKind.REGEX)
    public String value;
}

