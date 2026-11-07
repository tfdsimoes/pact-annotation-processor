package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactMin;
import java.util.List;

@PactDslBodyBuilder
public class InvalidArrayBounds {
    @PactArrayMatcher(PactArrayMode.MIN_LIKE)
    public List<String> missingMinimum;

    @PactArrayMatcher(value = PactArrayMode.MIN_MAX_LIKE, minSize = 3, maxSize = 2)
    public List<String> reversedBounds;

    @PactArrayMatcher(minSize = -2)
    public List<String> negativeMinimum;

    @PactArrayMatcher(maxSize = -2)
    public List<String> negativeMaximum;

    @PactMin(2)
    public List<String> numericAnnotationOnArray;

    @PactArrayMatcher(value = PactArrayMode.MIN_LIKE, minSize = 1001)
    public List<String> oversizedExampleCount;
}
