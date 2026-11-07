package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMax;
import com.pact.processor.annotation.PactMin;

@PactDslBodyBuilder
public class InvalidNumericConstraints {
    @PactExample("6")
    @PactMax(5)
    public int aboveMaximum;

    @PactExample("4")
    @PactMin(5)
    public int belowMinimum;

    @PactMin(10)
    @PactMax(2)
    public int conflictingBounds;

    @PactMin(1)
    public String nonNumeric;
}
