package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMapMatcher;
import java.util.List;
import java.util.Map;

@PactDslBodyBuilder
public class NestedMap {
    @PactMapMatcher(keyExample = "key")
    public Map<String, List<String>> values;
}
