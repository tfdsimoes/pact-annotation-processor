package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;
import java.util.List;

@PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
public class RootArrayContains {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"red", "blue"})
    public List<String> values;
}
