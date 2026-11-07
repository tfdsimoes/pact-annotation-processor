package fixture;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class RootValueMatchers {
    public enum State {
        READY,
        DONE
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class AutoBoolean {
        public boolean value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class AutoEnum {
        public State value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class AutoUuid {
        public UUID value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class EnumEquals {
        @PactMatcher(value = PactMatcherKind.EQUALS, example = "DONE")
        public State value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class StringType {
        @PactMatcher(value = PactMatcherKind.STRING_TYPE, example = "sample")
        public String value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class NumberType {
        @PactMatcher(value = PactMatcherKind.NUMBER_TYPE, example = "123.45")
        public BigDecimal value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class IntegerType {
        @PactMatcher(value = PactMatcherKind.INTEGER_TYPE, example = "42")
        public long value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class DecimalType {
        @PactMatcher(value = PactMatcherKind.DECIMAL_TYPE, example = "1.25")
        public double value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class BooleanType {
        @PactMatcher(value = PactMatcherKind.BOOLEAN_TYPE, example = "false")
        public boolean value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class Regex {
        @PactMatcher(value = PactMatcherKind.REGEX, regex = "[A-Z]+", example = "READY")
        public String value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class Date {
        @PactMatcher(value = PactMatcherKind.DATE, example = "31/12/2024", format = "dd/MM/yyyy")
        public LocalDate value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class Time {
        @PactMatcher(value = PactMatcherKind.TIME, example = "12:30:45", format = "HH:mm:ss")
        public LocalTime value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class DateTime {
        @PactMatcher(value = PactMatcherKind.DATETIME, example = "2024-01-02T03:04:05Z")
        public Instant value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class IpV4 {
        @PactMatcher(PactMatcherKind.IP_V4)
        public String value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class Id {
        @PactMatcher(value = PactMatcherKind.ID, example = "42")
        public long value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class Hex {
        @PactMatcher(value = PactMatcherKind.HEX, example = "AB12")
        public String value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class Uuid {
        @PactMatcher(value = PactMatcherKind.UUID, example = "00000000-0000-0000-0000-000000000001")
        public UUID value;
    }

    @PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
    public static class NullValue {
        @PactMatcher(PactMatcherKind.NULL_VALUE)
        public String value;
    }
}
