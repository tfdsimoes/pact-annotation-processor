package example;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class PetChoicesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.arrayContaining("pets", items -> {
        items.object(contained30 -> {
          contained30.stringType("name", "example");
          contained30.integerType("lives", (int) (long) 1);
        });
        items.object(contained31 -> {
          contained31.stringType("name", "example");
          contained31.integerType("barkVolume", (int) (long) 1);
        });
      });
    }).build();
  }

  public example.PetChoices buildExpectedInstance() {
    return createExpected_example_PetChoices();
  }

  private static example.PetChoices createExpected_example_PetChoices() {
    example.PetChoices result = new example.PetChoices();
    result.pets = java.util.Arrays.asList(createExpected_example_PetChoices_Cat(), createExpected_example_PetChoices_Dog());
    return result;
  }

  private static example.PetChoices.Cat createExpected_example_PetChoices_Cat() {
    example.PetChoices.Cat result = new example.PetChoices.Cat();
    result.name = "example";
    result.lives = 1;
    return result;
  }

  private static example.PetChoices.Dog createExpected_example_PetChoices_Dog() {
    example.PetChoices.Dog result = new example.PetChoices.Dog();
    result.name = "example";
    result.barkVolume = 1;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
