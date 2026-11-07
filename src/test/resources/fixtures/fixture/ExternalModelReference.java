package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import other.PackagePrivateModel;

@PactDslBodyBuilder
public class ExternalModelReference {
    public PackagePrivateModel value;
}
