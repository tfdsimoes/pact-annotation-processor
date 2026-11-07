package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RootArrayKinds {
    public enum State {
        READY,
        DONE
    }

    public static class Item {
        public String name;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayStrings {
        public List<String> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayBooleans {
        public List<Boolean> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayDecimals {
        public List<BigDecimal> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayDates {
        public List<LocalDate> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayUuids {
        public List<UUID> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayEnums {
        public List<State> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayObjects {
        public List<Item> values;
    }

    @PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "values")
    public static class ArrayNested {
        public List<List<Integer>> values;
    }
}
