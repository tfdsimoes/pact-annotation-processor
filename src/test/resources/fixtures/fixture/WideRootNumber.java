package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactRootType;
import java.math.BigInteger;

@PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
public class WideRootNumber {
    @PactExample("12345678901234567890")
    public BigInteger value;
}
