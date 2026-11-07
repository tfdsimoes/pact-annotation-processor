package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class GenericContainer<T> {
    public List<T> values;
}
