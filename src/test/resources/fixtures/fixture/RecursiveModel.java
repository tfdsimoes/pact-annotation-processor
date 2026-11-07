package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;

@PactDslBodyBuilder
public class RecursiveModel {
    public RecursiveModel child;
}
