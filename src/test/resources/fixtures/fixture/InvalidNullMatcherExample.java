package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMin;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.util.Date;

@PactDslBodyBuilder
public class InvalidNullMatcherExample {
    @PactMatcher(value = PactMatcherKind.NULL_VALUE, example = "ignored")
    public String value;

    @PactMatcher(PactMatcherKind.NULL_VALUE)
    public int primitiveValue;

    @PactMatcher(value = PactMatcherKind.NULL_VALUE, format = "yyyy")
    public Date formattedValue;

    @PactMin(1)
    @PactMatcher(PactMatcherKind.NULL_VALUE)
    public Integer constrainedValue;
}
