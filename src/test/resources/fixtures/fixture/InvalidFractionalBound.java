package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMin;
import java.math.BigDecimal;

@PactDslBodyBuilder
public class InvalidFractionalBound {
    @PactMin(1.5)
    public BigDecimal amount;
}
