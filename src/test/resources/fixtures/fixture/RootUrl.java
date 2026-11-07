package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;

@PactDslBodyBuilder(root = PactRootType.VALUE)
public class RootUrl {
    @PactMatcher(value = PactMatcherKind.URL, baseUrl = "https://example.test", pathFragments = "x")
    public String url;
}

