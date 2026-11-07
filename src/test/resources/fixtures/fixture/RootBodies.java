package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactRootType;
import java.util.List;

public class RootBodies {
    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class Scalar {
        @PactExample("root-value")
        public String value;

        @PactExample("not-root")
        public String ignored;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class Array {
        @PactArrayMatcher(minSize = 2)
        public List<Integer> values;

        @PactExample("not-root")
        public String ignored;
    }
}
