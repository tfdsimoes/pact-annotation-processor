package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;

@PactDslBodyBuilder
public class HelperNameCollision {
    public other.Outer.Inner nested;
    public other.Outer_Inner flat;
}
