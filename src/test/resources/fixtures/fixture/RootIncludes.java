package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;

@PactDslBodyBuilder(root = PactRootType.VALUE)
public class RootIncludes {
    @PactMatcher(value = PactMatcherKind.INCLUDES, example = "part")
    public String value;
}

