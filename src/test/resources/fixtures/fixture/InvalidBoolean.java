package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;

@PactDslBodyBuilder
public class InvalidBoolean {
    @PactExample("sometimes")
    public boolean enabled;
}

