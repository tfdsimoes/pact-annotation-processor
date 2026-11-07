package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.math.BigInteger;

@PactDslBodyBuilder
public class InvalidDecimalBigInteger {
    @PactMatcher(value = PactMatcherKind.DECIMAL_TYPE, example = "12345678901234567890")
    public BigInteger value;
}
