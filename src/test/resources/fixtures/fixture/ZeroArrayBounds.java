package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;
import java.util.Set;

@PactDslBodyBuilder
public class ZeroArrayBounds {
    @PactArrayMatcher(value = PactArrayMode.MAX_LIKE, maxSize = 0)
    public List<String> emptyStrings;

    @PactArrayMatcher(value = PactArrayMode.MIN_MAX_LIKE, minSize = 0, maxSize = 0)
    public List<Integer> emptyNumbers;

    @PactArrayMatcher(value = PactArrayMode.MAX_LIKE, maxSize = 0)
    public List<Address> emptyObjects;

    @PactArrayMatcher(value = PactArrayMode.MAX_LIKE, maxSize = 0)
    public int[][] emptyNestedArrays;

    @PactArrayMatcher(value = PactArrayMode.MAX_LIKE, maxSize = 0)
    public Set<String> emptySet;

    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MAX, maxSize = 0)
    public List<Boolean> emptyUnordered;

    public static class Address {
        public String city;
    }
}
