package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;

@PactDslBodyBuilder
public class InheritedFields extends InheritedBase {
    public String child;
}
