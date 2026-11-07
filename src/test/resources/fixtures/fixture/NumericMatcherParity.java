package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactMin;
import java.math.BigDecimal;
import java.math.BigInteger;

@PactDslBodyBuilder
public class NumericMatcherParity {
    @PactMatcher(value = PactMatcherKind.NUMBER_TYPE, example = "0.1000000001")
    public float numberFloat;

    @PactMatcher(value = PactMatcherKind.NUMBER_TYPE, example = "5.0F")
    @PactMin(5)
    public float suffixedFloat;

    @PactMatcher(value = PactMatcherKind.DECIMAL_TYPE, example = "0.1")
    public BigDecimal decimalValue;

    @PactMatcher(value = PactMatcherKind.DECIMAL_TYPE, example = "9007199254740992")
    public BigInteger exactInteger;

    @PactMatcher(value = PactMatcherKind.ID, example = "-9223372036854775808")
    public long minimumId;

    @PactMatcher(value = PactMatcherKind.ID, example = "9223372036854775807")
    public long maximumId;
}
