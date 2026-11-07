package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;
import java.time.LocalDateTime;

@PactDslBodyBuilder(root = PactRootType.VALUE)
public class RootTemporalLocalDateTime {
    @PactExample("2024-01-02T15:04:05")
    @PactMatcher(PactMatcherKind.DATETIME)
    public LocalDateTime value;
}
