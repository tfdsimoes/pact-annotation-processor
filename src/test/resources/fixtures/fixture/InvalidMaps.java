package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMin;
import com.pact.processor.annotation.PactMapMatcher;
import java.util.Map;

@PactDslBodyBuilder
public class InvalidMaps {
    @PactMapMatcher
    public Map<Integer, String> nonStringKey;

    public Map<String, String> missingAnnotation;

    @PactMapMatcher(keyExample = " ")
    public Map<String, Integer> blankKey;

    @PactMapMatcher
    @PactMin(1)
    public Map<String, Integer> constrainedMap;

    @PactMapMatcher
    public String annotationOnNonMap;
}
