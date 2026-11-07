package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@PactDslBodyBuilder
public class TemporalDefaults {
    public LocalDateTime localDateTime;
    public LocalTime localTime;
    public ZonedDateTime zonedDateTime;
    @PactExample(value = "2024-01-02T03:04:05+02:00", format = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public OffsetDateTime offsetDateTime;
}
