package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;

@PactDslBodyBuilder
public class NonFiniteNumericExamples {
    @PactExample("NaN")
    public float notANumber;

    @PactExample("Infinity")
    public double infinite;
}
