package fixture;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMapMatcher;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@PactDslBodyBuilder
public class Order {
    public String id;
    @PactExample("special")
    public String status;
    @PactMatcher(value = PactMatcherKind.STRING_TYPE, example = "named")
    public String displayName;
    public int quantity;
    public boolean valid;
    public Character initial;
    @PactExample("12345678901234567890")
    public BigInteger largeNumber;
    @PactExample("5000000000")
    public long largeLong;
    @PactExample("-5000000000")
    public long largeNegativeLong;
    @PactExample("-12345678901234567890")
    public BigInteger largeNegativeNumber;
    @PactExample("2.5")
    public float precision;
    public byte smallByte;
    public short smallShort;
    public OrderState state;
    @PactMatcher(value = PactMatcherKind.EQUALS, example = "locked")
    public String fixed;
    @PactMatcher(value = PactMatcherKind.NUMBER_TYPE, example = "2.5")
    public BigDecimal score;
    @PactMatcher(value = PactMatcherKind.INTEGER_TYPE, example = "3")
    public long count;
    @PactMatcher(value = PactMatcherKind.DECIMAL_TYPE, example = "1.25")
    public double ratio;
    @PactMatcher(value = PactMatcherKind.BOOLEAN_TYPE, example = "false")
    public Boolean enabled;
    @PactMatcher(value = PactMatcherKind.ID, example = "42")
    public int numericId;
    public Address address;
    @PactArrayMatcher(minSize = 2)
    public List<String> tags;
    public Set<String> categories;
    public Collection<Integer> numbers;
    @PactArrayMatcher(minSize = 2)
    public List<Integer> retries;
    @PactMapMatcher(keyExample = "sku-1")
    public Map<String, Integer> quantities;
    @PactMatcher(value = PactMatcherKind.REGEX, regex = "[A-Z]{2}-[0-9]+", example = "AB-42")
    public String reference;
    @PactMatcher(value = PactMatcherKind.IP_V4)
    public String ip;
    @PactMatcher(value = PactMatcherKind.HEX, example = "AB12")
    public String checksum;
    @PactMatcher(value = PactMatcherKind.UUID, example = "00000000-0000-0000-0000-000000000001")
    public UUID externalId;
    @PactMatcher(value = PactMatcherKind.INCLUDES, example = "fragile")
    public String description;
    @PactMatcher(value = PactMatcherKind.URL, baseUrl = "https://example.test", pathFragments = {"orders"})
    public String link;
    @PactMatcher(PactMatcherKind.NULL_VALUE)
    public String nullable;
    @PactMatcher(value = PactMatcherKind.DATE, example = "2024-01-02", format = "yyyy-MM-dd")
    public LocalDate created;
    @PactMatcher(value = PactMatcherKind.TIME, example = "12:30:45", format = "HH:mm:ss")
    public LocalTime started;
    @PactMatcher(value = PactMatcherKind.DATETIME, example = "2024-01-02T03:04:05Z")
    public Instant updated;
    public List<Address> addresses;
    @PactMapMatcher(keyExample = "id-1")
    public Map<String, Address> metadata;
    public int[][] coordinates;
    public List<List<Integer>> matrix;

    public static class Address {
        public String city;
        public int number;
    }

    public enum OrderState {
        NEW,
        PAID
    }
}
