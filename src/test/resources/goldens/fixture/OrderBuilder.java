package fixture;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import java.util.*;

public final class OrderBuilder {
  public DslPart build() {
    return LambdaDsl.newJsonBody(body -> {
      body.stringType("id", "example");
      body.stringType("status", "special");
      body.stringType("displayName", "named");
      body.integerType("quantity", (int) (long) 1);
      body.booleanType("valid", true);
      body.stringType("initial", "x");
      body.numberType("largeNumber", new java.math.BigInteger("12345678901234567890"));
      body.numberType("largeLong", 5000000000L);
      body.numberType("largeNegativeLong", -5000000000L);
      body.numberType("largeNegativeNumber", new java.math.BigInteger("-12345678901234567890"));
      body.decimalType("precision", (double) 2.5);
      body.integerType("smallByte", (int) (long) 1);
      body.integerType("smallShort", (int) (long) 1);
      body.stringValue("state", "NEW");
      body.stringValue("fixed", "locked");
      body.numberType("score", new java.math.BigDecimal("2.5"));
      body.integerType("count", (int) (long) 3);
      body.decimalType("ratio", (double) 1.25);
      body.booleanType("enabled", false);
      body.id("numericId", (long) 42L);
      body.object("address", nested3 -> {
        nested3.stringType("city", "example");
        nested3.integerType("number", (int) (long) 1);
      });
      body.minArrayLike("tags", 2, PactDslJsonRootValue.stringType("example"), 2);
      body.eachLike("categories", PactDslJsonRootValue.stringType("example"));
      body.eachLike("numbers", PactDslJsonRootValue.integerType((long) 1L));
      body.minArrayLike("retries", 2, PactDslJsonRootValue.integerType((long) 1L), 2);
      body.object("quantities", map3 -> {
        map3.eachValueLike("sku-1", PactDslJsonRootValue.integerType((long) 1L));
      });
      body.stringMatcher("reference", "[A-Z]{2}-[0-9]+", "AB-42");
      body.ipV4Address("ip");
      body.getPactDslObject().hexValue("checksum", "AB12");
      body.uuid("externalId", java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"));
      body.getPactDslObject().includesStr("description", "fragile");
      body.getPactDslObject().matchUrl("link", "https://example.test", "orders");
      body.nullValue("nullable");
      body.date("created", "yyyy-MM-dd", java.time.LocalDate.parse("2024-01-02", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd", java.util.Locale.ROOT)));
      body.time("started", "HH:mm:ss", java.util.Date.from(java.time.LocalTime.parse("12:30:45", java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale.ROOT)).atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneOffset.UTC).toInstant()), java.util.TimeZone.getTimeZone("UTC"));
      body.datetime("updated", "yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Date.from(java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT).parse("2024-01-02T03:04:05Z"))), java.util.TimeZone.getTimeZone("UTC"));
      body.eachLike("addresses", item3 -> {
        item3.stringType("city", "example");
        item3.integerType("number", (int) (long) 1);
      });
      body.object("metadata", map3 -> {
        map3.eachValueLike("id-1", value3 -> {
          value3.stringType("city", "example");
          value3.integerType("number", (int) (long) 1);
        });
      });
      body.eachArrayLike("coordinates", item3 -> {
        item3.integerType((long) 1L);
      });
      body.eachArrayLike("matrix", item3 -> {
        item3.integerType((long) 1L);
      });
    }).build();
  }

  public fixture.Order buildExpectedInstance() {
    return createExpected_fixture_Order();
  }

  private static fixture.Order createExpected_fixture_Order() {
    fixture.Order result = new fixture.Order();
    result.id = "example";
    result.status = "special";
    result.displayName = "named";
    result.quantity = 1;
    result.valid = true;
    result.initial = 'x';
    result.largeNumber = new java.math.BigInteger("12345678901234567890");
    result.largeLong = 5000000000L;
    result.largeNegativeLong = -5000000000L;
    result.largeNegativeNumber = new java.math.BigInteger("-12345678901234567890");
    result.precision = 2.5F;
    result.smallByte = 1;
    result.smallShort = 1;
    result.state = fixture.Order.OrderState.NEW;
    result.fixed = "locked";
    result.score = new java.math.BigDecimal("2.5");
    result.count = 3L;
    result.ratio = 1.25D;
    result.enabled = false;
    result.numericId = 42;
    result.address = createExpected_fixture_Order_Address();
    result.tags = java.util.Arrays.asList("example", "example");
    result.categories = new java.util.LinkedHashSet<>(java.util.Arrays.asList("example"));
    result.numbers = java.util.Arrays.asList(1);
    result.retries = java.util.Arrays.asList(1, 1);
    result.quantities = java.util.Collections.singletonMap("sku-1", 1);
    result.reference = "AB-42";
    result.ip = "127.0.0.1";
    result.checksum = "AB12";
    result.externalId = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");
    result.description = "fragile";
    result.link = "https://example.test/orders";
    result.nullable = null;
    result.created = java.time.LocalDate.parse("2024-01-02", java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd", java.util.Locale.ROOT));
    result.started = java.time.LocalTime.parse("12:30:45", java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss", java.util.Locale.ROOT));
    result.updated = java.time.Instant.from(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.ROOT).parse("2024-01-02T03:04:05Z"));
    result.addresses = java.util.Arrays.asList(createExpected_fixture_Order_Address());
    result.metadata = java.util.Collections.singletonMap("id-1", createExpected_fixture_Order_Address());
    result.coordinates = new int[][]{new int[]{1}};
    result.matrix = java.util.Arrays.asList(java.util.Arrays.asList(1));
    return result;
  }

  private static fixture.Order.Address createExpected_fixture_Order_Address() {
    fixture.Order.Address result = new fixture.Order.Address();
    result.city = "example";
    result.number = 1;
    return result;
  }

  private static PactDslJsonRootValue exactValue(Object value) {
    PactDslJsonRootValue result = new PactDslJsonRootValue();
    result.setValue(value);
    return result;
  }

}
