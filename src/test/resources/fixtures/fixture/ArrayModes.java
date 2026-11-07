package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class ArrayModes {
    @PactArrayMatcher(value = PactArrayMode.MIN_LIKE, minSize = 2)
    public List<String> minimum;
    @PactArrayMatcher(value = PactArrayMode.MAX_LIKE, maxSize = 4)
    public List<Integer> maximum;
    @PactArrayMatcher(value = PactArrayMode.MIN_MAX_LIKE, minSize = 1, maxSize = 3)
    public List<Boolean> bounded;
    @PactArrayMatcher(PactArrayMode.UNORDERED)
    public List<String> unordered;
    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MIN, minSize = 2)
    public List<String> unorderedMinimum;
    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MAX, maxSize = 4)
    public List<String> unorderedMaximum;
    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MIN_MAX, minSize = 1, maxSize = 3)
    public List<String> unorderedBounded;
    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MIN, minSize = 2)
    public int[][] nested;
}

