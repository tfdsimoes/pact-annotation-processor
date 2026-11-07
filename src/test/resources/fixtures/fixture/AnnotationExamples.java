package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactExclude;
import java.time.LocalDate;

@PactDslBodyBuilder
public class AnnotationExamples {
    @PactExample(value = "31/12/2024", format = "dd/MM/yyyy")
    public LocalDate formattedDate;

    @PactExample("configured")
    public String text;

    @PactExclude
    public String excluded;

    @PactExclude
    public String alsoExcluded;
}
