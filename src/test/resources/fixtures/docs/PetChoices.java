package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class PetChoices {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS,
            containsTypes = {"example.PetChoices.Cat", "example.PetChoices.Dog"})
    public List<Pet> pets;

    public interface Pet {
        String getName();
    }

    public static class Cat implements Pet {
        public String name;
        public int lives;

        @Override
        public String getName() {
            return name;
        }
    }

    public static class Dog implements Pet {
        public String name;
        public int barkVolume;

        @Override
        public String getName() {
            return name;
        }
    }
}
