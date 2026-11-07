package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class InvalidContainsTypesMode {
    @PactArrayMatcher(containsTypes = {"fixture.InvalidContainsTypesMode.Cat"})
    public List<Animal> pets;

    public static class Animal {
        public String name;
    }

    public static class Cat extends Animal {
    }
}
