package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactArrayMatcher;
import java.util.List;

@PactDslBodyBuilder
public class ConstraintModels {
    @PactArrayMatcher(minSize = 2)
    public List<String> minimum;

    @PactArrayMatcher(minSize = 3, maxSize = 5)
    public List<Integer> bounded;

    @PactArrayMatcher(maxSize = 4)
    public List<String> maximum;

    @PactArrayMatcher(minSize = 2)
    public String[] arrayMinimum;
}
