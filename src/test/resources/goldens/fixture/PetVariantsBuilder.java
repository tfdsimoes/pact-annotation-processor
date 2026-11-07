package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class PetVariantsBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.arrayContaining("pets", items -> {
        items.object(contained30 -> {
          contained30.stringType("name", "example");
          contained30.integerType("lives", (int) (long) 1);
        });
        items.object(contained31 -> {
          contained31.stringType("name", "example");
          contained31.booleanType("trained", true);
        });
      });
    }).build();
  }

  public fixture.PetVariants buildExpectedInstance() {
    return createExpected_fixture_PetVariants();
  }

  private static fixture.PetVariants createExpected_fixture_PetVariants() {
    fixture.PetVariants result = new fixture.PetVariants();
    result.pets = java.util.Arrays.asList(createExpected_fixture_PetVariants_Cat(), createExpected_fixture_PetVariants_Dog());
    return result;
  }

  private static fixture.PetVariants.Cat createExpected_fixture_PetVariants_Cat() {
    fixture.PetVariants.Cat result = new fixture.PetVariants.Cat();
    result.name = "example";
    result.lives = 1;
    return result;
  }

  private static fixture.PetVariants.Dog createExpected_fixture_PetVariants_Dog() {
    fixture.PetVariants.Dog result = new fixture.PetVariants.Dog();
    result.name = "example";
    result.trained = true;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
