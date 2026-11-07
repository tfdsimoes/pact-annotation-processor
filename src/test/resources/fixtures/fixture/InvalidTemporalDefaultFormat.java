package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.time.LocalDateTime;

@PactDslBodyBuilder
public class InvalidTemporalDefaultFormat {
    @PactMatcher(value = PactMatcherKind.DATETIME, format = "yyyy")
    public LocalDateTime value;
}
