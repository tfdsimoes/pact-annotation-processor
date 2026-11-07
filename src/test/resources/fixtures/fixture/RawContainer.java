package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class RawContainer {
    @SuppressWarnings("rawtypes")
    public List values;
}
