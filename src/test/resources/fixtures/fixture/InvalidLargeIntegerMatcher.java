package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactMin;

@PactDslBodyBuilder
public class InvalidLargeIntegerMatcher {
    @PactMatcher(value = PactMatcherKind.INTEGER_TYPE, example = "5000000000")
    public long value;

    @PactMatcher(PactMatcherKind.INTEGER_TYPE)
    @PactMin(5000000000L)
    public long constrainedDefault;
}
