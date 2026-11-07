package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import java.time.LocalDate;

@PactDslBodyBuilder
public class InvalidTemporalExample {
    @PactExample(value = "not-a-date", format = "dd/MM/yyyy")
    public LocalDate date;
}

