package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactRootType;
import java.time.LocalTime;

@PactDslBodyBuilder(root = PactRootType.VALUE)
public class RootTemporalValue {
    @PactExample(value = "03:04 PM", format = "hh:mm a")
    public LocalTime value;
}
