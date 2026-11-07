package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;

@PactDslBodyBuilder
public class ConflictingExamplesWithMatcher {
    @PactExample("current")
    @PactMatcher(PactMatcherKind.STRING_TYPE)
    public String value;

    @PactExample("annotation")
    @PactMatcher(value = PactMatcherKind.STRING_TYPE, example = "matcher")
    public String matcherConflict;
}
