package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.Set;

@PactDslBodyBuilder
public class InvalidSetMinimum {
    @PactArrayMatcher(value = PactArrayMode.MIN_LIKE, minSize = 2)
    public Set<String> repeatedMinimum;

    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"red", "red"})
    public Set<String> duplicateContains;
}
