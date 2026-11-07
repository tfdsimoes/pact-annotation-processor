package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;
import java.util.Date;

@PactDslBodyBuilder(root = PactRootType.VALUE)
public class RootTemporalDate {
    @PactMatcher(value = PactMatcherKind.DATE, example = "2024-01-02")
    public Date value;
}
