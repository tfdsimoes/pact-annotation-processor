package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;

@com.pact.processor.annotation.PactDslBodyBuilder
public class InvalidNumeric {
    @PactExample("3")
    @com.pact.processor.annotation.PactMin(5)
    public int quantity;
}
