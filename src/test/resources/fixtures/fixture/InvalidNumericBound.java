package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMin;

@PactDslBodyBuilder
public class InvalidNumericBound {
    @PactMin(2147483648L)
    public int value;
}
