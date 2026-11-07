package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.util.Date;

@PactDslBodyBuilder
public class DateValues {
    @PactExample(value = "02/01/2024", format = "dd/MM/yyyy")
    public Date date;

    @PactMatcher(value = PactMatcherKind.TIME, example = "03:04 PM", format = "hh:mm a")
    public Date time;

    @PactMatcher(value = PactMatcherKind.DATETIME, example = "02/01/2024 03:04:05 +00:00",
            format = "dd/MM/yyyy HH:mm:ss XXX")
    public Date datetime;
}
