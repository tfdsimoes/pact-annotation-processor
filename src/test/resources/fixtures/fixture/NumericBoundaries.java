package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactMin;
import com.pact.processor.annotation.PactMax;
import java.util.List;

@PactDslBodyBuilder
public class NumericBoundaries {
    @PactMin(-5)
    @PactMax(-2)
    public int negativeRange;

    @PactMax(-1)
    public int maxMinusOne;

    @PactMin(5)
    @PactMax(10)
    public int positiveRange;

    @PactArrayMatcher(minSize = 3)
    public List<Integer> values;

    @PactMatcher(value = PactMatcherKind.INTEGER_TYPE, example = "-2147483648")
    public int minimumInteger;

    @PactMatcher(value = PactMatcherKind.INTEGER_TYPE, example = "2147483647")
    public int maximumInteger;
}
