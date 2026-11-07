package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import java.util.Date;

@PactDslBodyBuilder
public class InvalidDateExample {
    @PactExample(value = "not-a-date", format = "dd/MM/yyyy")
    public Date date;
}
