package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;
import java.util.List;

public class RootArrayModes {
    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayEach {
        @PactArrayMatcher(PactArrayMode.EACH_LIKE)
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayMin {
        @PactArrayMatcher(value = PactArrayMode.MIN_LIKE, minSize = 2)
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayMax {
        @PactArrayMatcher(value = PactArrayMode.MAX_LIKE, maxSize = 3)
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayMinMax {
        @PactArrayMatcher(value = PactArrayMode.MIN_MAX_LIKE, minSize = 1, maxSize = 3)
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayUnordered {
        @PactArrayMatcher(PactArrayMode.UNORDERED)
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayUnorderedMin {
        @PactArrayMatcher(value = PactArrayMode.UNORDERED_MIN, minSize = 2)
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayUnorderedMax {
        @PactArrayMatcher(value = PactArrayMode.UNORDERED_MAX, maxSize = 3)
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayUnorderedMinMax {
        @PactArrayMatcher(value = PactArrayMode.UNORDERED_MIN_MAX, minSize = 1, maxSize = 3)
        public List<String> values;
    }
}
