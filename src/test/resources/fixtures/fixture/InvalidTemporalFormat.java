package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import java.time.LocalDate;

@PactDslBodyBuilder
public class InvalidTemporalFormat {
    @PactExample(value = "2024-01-02", format = "yyyy-MM-dd'")
    public LocalDate date;
}

