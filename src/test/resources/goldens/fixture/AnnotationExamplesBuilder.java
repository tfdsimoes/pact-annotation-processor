package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class AnnotationExamplesBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.date("formattedDate", "dd/MM/yyyy", java.time.LocalDate.parse("31/12/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT)));
      body.stringType("text", "configured");
    }).build();
  }

  public fixture.AnnotationExamples buildExpectedInstance() {
    return createExpected_fixture_AnnotationExamples();
  }

  private static fixture.AnnotationExamples createExpected_fixture_AnnotationExamples() {
    fixture.AnnotationExamples result = new fixture.AnnotationExamples();
    result.formattedDate = java.time.LocalDate.parse("31/12/2024", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy", java.util.Locale.ROOT));
    result.text = "configured";
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
