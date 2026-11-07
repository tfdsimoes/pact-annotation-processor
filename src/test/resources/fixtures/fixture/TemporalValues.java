package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@PactDslBodyBuilder
public class TemporalValues {
    @PactExample(value = "02/01/2024", format = "dd/MM/yyyy")
    public LocalDate date;
    @PactExample(value = "03:04 PM", format = "hh:mm a")
    public LocalTime time;
    @PactExample(value = "02-01-2024 03:04:05", format = "dd-MM-yyyy HH:mm:ss")
    public LocalDateTime datetime;
    @PactExample(value = "02/01/2024 03:04:05 +00:00", format = "dd/MM/yyyy HH:mm:ss XXX")
    public Instant instant;
}

