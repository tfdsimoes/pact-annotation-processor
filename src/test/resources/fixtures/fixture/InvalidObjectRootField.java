package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;

@PactDslBodyBuilder(root = PactRootType.OBJECT, rootField = "value")
public class InvalidObjectRootField {
    public String value;
}
