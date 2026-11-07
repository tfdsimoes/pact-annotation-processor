package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class InvalidArrayContainsModes {
    @PactArrayMatcher(PactArrayMode.ARRAY_CONTAINS)
    public List<String> missingExamples;

    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"cat"},
            containsTypes = {"fixture.InvalidArrayContainsModes.Cat"})
    public List<Animal> mixedExamples;

    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS,
            containsTypes = {"fixture.InvalidArrayContainsModes.Cat", "fixture.InvalidArrayContainsModes.Cat"})
    public List<Animal> duplicateAlternatives;

    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS,
            containsTypes = {"fixture.InvalidArrayContainsModes.Unrelated"})
    public List<Animal> unassignableAlternative;

    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"example"})
    public List<Animal> scalarAlternativeForObject;

    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, minSize = 1, contains = {"cat"})
    public List<String> alternativesWithBounds;

    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS,
            containsTypes = {"fixture.InvalidArrayContainsModes.AbstractAnimal"})
    public List<Animal> abstractAlternative;

    @PactArrayMatcher(value = PactArrayMode.EACH_LIKE, contains = {"cat"})
    public List<String> examplesOnEachLike;

    @PactArrayMatcher(value = PactArrayMode.EACH_LIKE,
            containsTypes = {"fixture.InvalidArrayContainsModes.Cat"})
    public List<Animal> typesOnEachLike;

    public static class Animal {
        public String name;
    }

    public abstract static class AbstractAnimal extends Animal {
    }

    public static class Cat extends Animal {
    }

    public static class Unrelated {
        public String name;
    }
}
