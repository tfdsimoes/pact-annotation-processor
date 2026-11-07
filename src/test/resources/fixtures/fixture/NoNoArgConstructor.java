package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;

@PactDslBodyBuilder
public class NoNoArgConstructor {
    public NoNoArgConstructor(String value) {
    }

    public String value;
}
