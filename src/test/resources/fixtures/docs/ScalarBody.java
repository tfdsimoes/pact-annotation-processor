package example;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactRootType;

@PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
public class ScalarBody {
    @PactExample("accepted")
    public String value;
}
