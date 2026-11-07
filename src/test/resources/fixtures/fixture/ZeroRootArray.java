package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;
import java.util.List;

@PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
public class ZeroRootArray {
    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MAX, maxSize = 0)
    public List<Item> values;

    public static class Item {
        public String name;
    }
}
