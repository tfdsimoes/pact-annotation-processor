package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class InvalidContains {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"not-a-number"})
    public List<Integer> values;
}

