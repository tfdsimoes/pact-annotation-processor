package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;
import java.util.List;

public class InvalidRootShapes {
    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class ValueCollection {
        public List<String> value;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "value")
    public static class ArrayScalar {
        public String value;
    }
}
