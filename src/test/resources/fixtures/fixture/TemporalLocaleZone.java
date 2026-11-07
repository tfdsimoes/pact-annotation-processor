package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.util.Date;

@PactDslBodyBuilder
public class TemporalLocaleZone {
    @PactExample(value = "Jan 02, 2024", format = "MMM dd, yyyy")
    public Date date;

    @PactMatcher(value = PactMatcherKind.TIME, example = "03:04 PM", format = "hh:mm a")
    public Date time;

    @PactMatcher(value = PactMatcherKind.DATETIME, example = "Jan 02, 2024 03:04 PM",
            format = "MMM dd, yyyy hh:mm a")
    public Date datetime;
}
