package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMapMatcher;
import java.util.Map;

@PactDslBodyBuilder
public class InvalidRawMap {
    @PactMapMatcher
    public Map values;
}
