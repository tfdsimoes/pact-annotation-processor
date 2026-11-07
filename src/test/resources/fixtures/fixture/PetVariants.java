package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class PetVariants {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS,
            containsTypes = {"fixture.PetVariants.Cat", "fixture.PetVariants.Dog"})
    public List<Pet> pets;

    public abstract static class Pet {
        public String name;
    }

    public static class Cat extends Pet {
        public int lives;
    }

    public static class Dog extends Pet {
        public boolean trained;
    }
}

