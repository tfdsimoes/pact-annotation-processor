package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.Set;

@PactDslBodyBuilder
public class SetArrayContains {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"red", "blue"})
    public Set<String> colors;
}
