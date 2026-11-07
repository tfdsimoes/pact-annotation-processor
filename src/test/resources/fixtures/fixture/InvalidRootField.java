package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;

@PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "missing")
public class InvalidRootField {
    public String value;
}
