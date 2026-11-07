package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.util.UUID;

@PactDslBodyBuilder
public class InvalidMatcherTypes {
    @PactMatcher(PactMatcherKind.IP_V4)
    public UUID ipAddress;

    @PactMatcher(PactMatcherKind.HEX)
    public UUID checksum;

    @PactMatcher(PactMatcherKind.UUID)
    public char identifier;
}
