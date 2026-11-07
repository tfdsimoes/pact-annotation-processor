package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;
import java.util.List;

@PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "orders")
public class ArrayBody {
    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MIN, minSize = 2)
    public List<Order> orders;
}
