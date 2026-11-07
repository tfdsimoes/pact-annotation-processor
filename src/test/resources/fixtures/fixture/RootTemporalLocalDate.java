package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;
import java.time.LocalDate;

@PactDslBodyBuilder(root = PactRootType.VALUE)
public class RootTemporalLocalDate {
    @PactExample("2024-01-02")
    @PactMatcher(PactMatcherKind.DATE)
    public LocalDate value;
}
