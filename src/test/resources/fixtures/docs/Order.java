package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.util.List;

@PactDslBodyBuilder
public class Order {
    @PactExample("ORD-42")
    public String id;

    @PactMatcher(value = PactMatcherKind.REGEX, regex = "[A-Z]{3}-[0-9]+", example = "ORD-42")
    public String reference;

    @PactArrayMatcher(minSize = 1, maxSize = 5)
    public List<String> tags;

    public Address shippingAddress;

    public static class Address {
        public String city;
        public int postalCode;
    }
}
