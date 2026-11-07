package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;

@PactDslBodyBuilder
public class NoWritableField {
    private String value;
}
