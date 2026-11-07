package example;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactExclude;
import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactMax;
import com.pact.processor.annotation.PactMin;
import com.pact.processor.annotation.PactMapMatcher;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@PactDslBodyBuilder
public class AdvancedOrder {
    @PactMapMatcher(keyExample = "sku-1")
    public Map<String, Integer> quantities;

    @PactExample(value = "02/01/2024", format = "dd/MM/yyyy")
    public LocalDate created;

    @PactMin(-5)
    @PactMax(10)
    public int adjustment;

    @PactArrayMatcher(minSize = 2)
    public List<String> tags;

    @PactExclude
    public String internalOnly;
}
