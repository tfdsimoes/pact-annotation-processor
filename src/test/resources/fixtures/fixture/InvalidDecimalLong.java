package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;

@PactDslBodyBuilder
public class InvalidDecimalLong {
    @PactMatcher(value = PactMatcherKind.DECIMAL_TYPE, example = "9007199254740993")
    public long value;
}
