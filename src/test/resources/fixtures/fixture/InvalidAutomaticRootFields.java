package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;

public class InvalidAutomaticRootFields {
    @PactDslBodyBuilder(root = PactRootType.VALUE)
    public static class MultipleFields {
        public String first;
        public String second;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY)
    public static class NoFields {
    }
}
