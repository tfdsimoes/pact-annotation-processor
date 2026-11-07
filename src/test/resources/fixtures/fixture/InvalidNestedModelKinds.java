package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;

@PactDslBodyBuilder
public class InvalidNestedModelKinds {
    public Contract contract;
    public AbstractContract abstractContract;
    public EmptyState emptyState;
    public NonStaticModel nonStaticModel;
    public RecordModel recordModel;

    public interface Contract {
        String value();
    }

    public abstract static class AbstractContract {
        public String value;
    }

    public enum EmptyState {
        ;
    }

    public class NonStaticModel {
        public String value;
    }

    public record RecordModel(String value) {
    }
}
