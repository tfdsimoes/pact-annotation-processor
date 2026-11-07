package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class Choice {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"red", "blue"})
    public List<String> colors;
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"2", "7"})
    public List<Integer> quantities;
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"true", "false"})
    public List<Boolean> flags;
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"READY", "DONE"})
    public List<Status> statuses;

    public enum Status {
        READY,
        DONE
    }
}

