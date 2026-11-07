# Pact Annotation Processor

Generate Pact JVM JSON `DslPart` bodies and matching example model instances from annotated Java classes. The processor targets Java 21 and Pact JVM consumer 4.7.6. Generation happens at compile time; the consumer project remains responsible for creating Pact interactions, running provider verification, and selecting a Pact test framework.

## What gets generated

Annotate a model with `@PactDslBodyBuilder`. For a model named `Order`, compilation generates `OrderBuilder` in the same package with:

- `DslPart build()` — a Pact JSON body with example values and matching rules.
- `Order buildExpectedInstance()` — a model populated with the same deterministic example values.

Generated code is ordinary Java and can be inspected under the compiler's generated-source output. The processor does not modify the model class.

## Installation

The project requires a Java 21 compiler. For local development, install the processor artifact into your local Maven repository from this project:

```shell
./mvnw -B install
```

On Windows PowerShell, run `.\mvnw.cmd -B install`.

Then add the processor and Pact consumer DSL to the consuming project's `pom.xml`. The processor must be on the compiler's annotation-processor path; the Pact consumer dependency must be available to compile generated code.

The processor runs for types annotated with `@PactDslBodyBuilder`. Field annotations such as `@PactMatcher`, `@PactExample`, `@PactArrayMatcher`, `@PactMapMatcher`, `@PactExclude`, `@PactMin`, and `@PactMax` configure fields included in a processed model; they do not trigger builder generation by themselves. The same applies to supported inherited fields. Annotations on unrelated types are accepted by the compiler but have no effect on generated code.

```xml
<dependencies>
  <dependency>
    <groupId>com.pact.processor</groupId>
    <artifactId>pact-annotation-processor</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>au.com.dius.pact</groupId>
    <artifactId>consumer</artifactId>
    <version>4.7.6</version>
  </dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.13.0</version>
      <configuration>
        <release>21</release>
        <annotationProcessorPaths>
          <path>
            <groupId>com.pact.processor</groupId>
            <artifactId>pact-annotation-processor</artifactId>
            <version>1.0-SNAPSHOT</version>
          </path>
        </annotationProcessorPaths>
      </configuration>
    </plugin>
  </plugins>
</build>
```

This repository currently builds `1.0-SNAPSHOT`; install it locally using the command above. Release publication is not configured here. When a release is published to a Maven repository, use that release version and repository instead. If the builder is not generated, check that the model has `@PactDslBodyBuilder`, the compiler is using Java 21, and the processor artifact is present in `annotationProcessorPaths`.

## First object-body example

This example is also compiled by the processor test suite from `src/test/resources/fixtures/docs/Order.java`.

```java
package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.util.List;

@PactDslBodyBuilder
public class Order {
    @PactExample("ORD-42")
    public String id;

    @PactMatcher(value = PactMatcherKind.REGEX, regex = "[A-Z]{3}-[0-9]+", example = "ORD-42")
    public String reference;

    @PactArrayMatcher(minSize = 1, maxSize = 5)
    public List<String> tags;

    public Address shippingAddress;

    public static class Address {
        public String city;
        public int postalCode;
    }
}
```

The default root is `OBJECT`. In a consumer test in the `example` package, use the generated builder wherever the Pact DSL accepts a `DslPart`, and use the example model for the expected request or response:

```java
import au.com.dius.pact.consumer.dsl.DslPart;

OrderBuilder orderBuilder = new OrderBuilder();
DslPart pactBody = orderBuilder.build();
Order expectedModel = orderBuilder.buildExpectedInstance();

// Use pactBody in the request/response body for the interaction.
// Compare the provider response with expectedModel using your project's JSON/model tooling.
```

`@PactArrayMatcher(minSize = 1, maxSize = 5)` selects a bounded array rule even though its mode defaults to `EACH_LIKE`; explicit size bounds promote the default mode to the corresponding bounded mode.

## Root body examples

For a root scalar or root array, identify the model field that supplies the body. `rootField` is optional only when there is exactly one non-excluded model field; setting it on an `OBJECT` root is an error. These are separate source files and are compile-tested from `src/test/resources/fixtures/docs/ScalarBody.java` and `ArrayBody.java`.

```java
package example;

import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactRootType;

@PactDslBodyBuilder(root = PactRootType.VALUE, rootField = "value")
public class ScalarBody {
    @PactExample("accepted")
    public String value;
}
```

```java
package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactRootType;
import java.util.List;

@PactDslBodyBuilder(root = PactRootType.ARRAY, rootField = "orders")
public class ArrayBody {
    @PactArrayMatcher(value = PactArrayMode.UNORDERED_MIN, minSize = 2)
    public List<Order> orders;
}
```

`VALUE` roots require a supported scalar or enum field. `ARRAY` roots require a Java array or supported `Collection` field. Only `rootField` supplies the non-object body; other non-excluded model fields are still validated and populated in `buildExpectedInstance()`.

## Annotation reference

| Annotation | Target | Members and behavior |
|---|---|---|
| `@PactDslBodyBuilder` | Class | `root` defaults to `OBJECT` and accepts `OBJECT`, `VALUE`, or `ARRAY`. `rootField` defaults to `""`; non-object roots use it to select a field. If omitted, exactly one non-excluded field must be available. |
| `@PactExample` | Field | Required `value` supplies the example. Optional `format` controls parsing and Pact's matcher format for supported date/time types only. |
| `@PactMatcher` | Field | `value` defaults to `AUTO`; `example`, `regex`, `format`, `baseUrl`, and `pathFragments` supply matcher arguments. Arguments unrelated to the selected matcher are diagnosed instead of ignored. |
| `@PactArrayMatcher` | Field | `value` defaults to `EACH_LIKE`; `minSize` and `maxSize` default to `-1` (unspecified) and are the only array-size bounds supported. `contains` supplies scalar/enum alternatives and `containsTypes` supplies object alternatives for `ARRAY_CONTAINS`. |
| `@PactMapMatcher` | Field | Required for `Map` properties. `keyExample` defaults to `"key"` and chooses the representative key for generated map examples. |
| `@PactExclude` | Field | Excludes the field from the Pact body and expected instance, and skips validation of that field. |
| `@PactMin`, `@PactMax` | Numeric field | Optional `long` thresholds that constrain or select the generated example only; they do not add a Pact numeric range matcher. Fractional thresholds cannot be expressed, including for `float`, `double`, or `BigDecimal` fields. |

When both `@PactMin` and `@PactMax` are present, the minimum must not exceed the maximum. A numeric example supplied by `@PactExample` or `@PactMatcher.example` must fall within those bounds. For temporal formats, precedence is `@PactExample.format`, then `@PactMatcher.format`, then the type-specific default. Exclusion takes precedence over other field validation.

### Scalar matcher compatibility

All examples are validated at compile time, including the default sample selected when no example is provided. Unsupported matcher/type combinations and invalid matcher arguments are compile-time errors.

| `PactMatcherKind` | Supported property types and notes |
|---|---|
| `AUTO` | Any supported scalar or enum. Selects an exact/default matcher by type; integral examples in the 32-bit range use `INTEGER_TYPE`, wider integral examples use `NUMBER_TYPE`, and decimal types use `DECIMAL_TYPE`. |
| `EQUALS` | Supported scalar or enum; emits an exact example. |
| `STRING_TYPE` | `String`, `char`/`Character`, or `UUID`. |
| `NUMBER_TYPE` | Numeric Java types, including `BigInteger` and `BigDecimal`. |
| `INTEGER_TYPE` | Integral Java types. The Pact object DSL example must fit in a signed 32-bit integer; otherwise use `NUMBER_TYPE` or `AUTO`. |
| `DECIMAL_TYPE` | Numeric Java types. Integral, `BigInteger`, and `BigDecimal` samples must be finite and exactly representable by Pact's `double` example. Floating-point samples must be finite. |
| `BOOLEAN_TYPE` | `boolean` or `Boolean`. |
| `REGEX` | `String`, `char`/`Character`, or `UUID`. Requires a non-blank valid regex; the selected example must match it. |
| `DATE` | `LocalDate` or `java.util.Date`. |
| `TIME` | `LocalTime` or `java.util.Date`. |
| `DATETIME` | `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`, `Instant`, or `java.util.Date`. |
| `IP_V4` | `String`; Pact supplies its IPv4 example. A custom example is not accepted. |
| `ID` | Integral type; examples must fit in a signed 64-bit integer. |
| `HEX` | `String` or `char`/`Character`; examples contain hexadecimal characters only. |
| `UUID` | `String` or `UUID`; examples must be valid UUID strings. |
| `INCLUDES` | `String` or `char`/`Character`; requires a non-empty substring in `@PactMatcher.example`. Not supported for array/map values or root bodies. |
| `URL` | `String`; requires a non-blank `baseUrl` and at least one `pathFragments` entry. A supplied example must equal the configured URL. Not supported for root bodies. |
| `NULL_VALUE` | A non-primitive supported scalar or enum. Do not provide an example. |

For `DATE`, `TIME`, and `DATETIME`, use `@PactExample.format` or `@PactMatcher.format` when the example uses a custom pattern. The processor parses patterns with `Locale.ROOT`. Pact JVM 4.7.6 formats textual date/time symbols using the consuming JVM's default locale, so use numeric patterns for locale-independent Pact body examples. Object-body generation passes UTC explicitly for local-to-instant conversions and preserves explicit offsets/zones where available. The root-value DSL has no explicit time-zone overload: root `Date`, `LocalDate`, `LocalTime`, and `LocalDateTime` conversions therefore use the consuming JVM's default time zone.

`AUTO` uses stable examples when none are specified: for example, `"example"` for strings, `"x"` for characters, `true` for booleans, `1` for integral values, `1.5` for decimal values, the first enum constant, and a fixed UUID. Matcher-specific examples include `127.0.0.1` for IPv4 and `AB12` (or `A` for a character) for hexadecimal matching. Numeric constraints can adjust the default numeric example.

## Array and collection matching

`@PactArrayMatcher` applies to Java arrays and supported `Collection` properties (`List`, `Collection`, and `Set`).

| Mode | Required bound(s) | Pact behavior |
|---|---|---|
| `EACH_LIKE` | None | Each item matches the representative item. |
| `MIN_LIKE` | Minimum | At least the minimum number of items. |
| `MAX_LIKE` | Maximum | At most the maximum number of items. |
| `MIN_MAX_LIKE` | Minimum and maximum | Item count is bounded at both ends. |
| `UNORDERED` | None | Items are matched without requiring their order. |
| `UNORDERED_MIN` | Minimum | Unordered items with a minimum size. |
| `UNORDERED_MAX` | Maximum | Unordered items with a maximum size. |
| `UNORDERED_MIN_MAX` | Minimum and maximum | Unordered items with both size bounds. |
| `ARRAY_CONTAINS` | `contains` or `containsTypes` | Requires one or more alternative examples; see below. |

Array bounds are configured only with `@PactArrayMatcher.minSize` and `maxSize`; Bean Validation annotations are not used by the processor. Negative values other than the `-1` unspecified default, a minimum greater than the maximum, and minimum sizes above 1,000 are rejected. Zero is valid; a maximum size of zero generates an empty example array. A bound on the default `EACH_LIKE` mode automatically selects the matching bounded mode. For minimum-sized arrays, the generated expected instance contains enough representative items to meet the minimum. Since a `Set` cannot contain duplicate examples, Set properties reject generated minimum sizes greater than one; scalar/enum `ARRAY_CONTAINS` examples on a Set must also be unique.

### V4 `ARRAY_CONTAINS`

Scalar and enum alternatives are parsed according to the array element type. For example, put this field on an object-root model:

```java
package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class NumberChoices {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS, contains = {"2", "7"})
    public List<Integer> acceptedValues;
}
```

Model alternatives must name concrete model classes assignable to the array element type. Each alternative must have an accessible no-argument constructor and writable supported fields:

```java
package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import java.util.List;

@PactDslBodyBuilder
public class PetChoices {
    @PactArrayMatcher(value = PactArrayMode.ARRAY_CONTAINS,
            containsTypes = {"example.PetChoices.Cat", "example.PetChoices.Dog"})
    public List<Pet> pets;

    public interface Pet {
        String getName();
    }

    public static class Cat implements Pet {
        public String name;
        public int lives;

        @Override
        public String getName() {
            return name;
        }
    }

    public static class Dog implements Pet {
        public String name;
        public int barkVolume;

        @Override
        public String getName() {
            return name;
        }
    }
}
```

Use either `contains` or `containsTypes`, not both. Field-level matcher/example annotations and size bounds cannot be combined with `ARRAY_CONTAINS`. It is supported only for array properties of object-root models and requires Pact specification V4 in the consuming interaction.

Root-array containment is deliberately rejected. Pact JVM consumer 4.7.6's `PactDslJsonArray.arrayContaining(String)` throws `UnsupportedOperationException` (“arrayContaining is not currently supported for arrays”), and the Lambda DSL has no root-array alternative. Root arrays do support each-like, size-bounded, and unordered modes.

## Maps, examples, constraints, and exclusions

Maps must be declared as `Map<String, T>` and use `@PactMapMatcher`. A map value can be scalar, enum, or a supported model type; the value type itself cannot be another map, array, or collection. Object-valued entries follow the normal supported model rules. The generated body and expected instance contain one representative map entry:

```java
package example;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExclude;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMapMatcher;
import com.pact.processor.annotation.PactMax;
import com.pact.processor.annotation.PactMin;
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
```

For supported `java.time` and `java.util.Date` values, `@PactExample.format` controls parsing and the Pact matcher format. Invalid values or patterns fail compilation. `@PactMin` / `@PactMax` on numeric fields select or validate the generated example; they do not create a Pact numeric range matcher. Their thresholds are integral `long` values, so fractional thresholds cannot be expressed for `float`, `double`, or `BigDecimal`. Use `@PactArrayMatcher(minSize = ..., maxSize = ...)` for collection size bounds. `@PactExclude` removes a field from both the generated body and expected instance and exempts it from validation:

```java
@PactExclude
public String internalOnly;
```

## Supported model shapes

- Object roots, root scalar values, and root arrays.
- Nested concrete POJOs, enums, inherited instance fields, Java arrays, nested arrays, and parameterized `List`, `Collection`, and `Set` properties.
- String-keyed maps with scalar, enum, or object values.
- Primitive and boxed scalar values, `BigInteger`, `BigDecimal`, `UUID`, `Date`, and `LocalDate`, `LocalTime`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`, and `Instant`.
- Public, package-private, or protected model members when accessible from the generated builder's package. Model classes need an accessible no-argument constructor. Private fields need an accessible setter; final fields are not supported.
- Static and transient fields are ignored. Excluded fields are omitted from generated bodies and expected instances.

Nested model classes must be static and concrete. Recursive model graphs, records, interfaces, abstract classes, raw/wildcard/type-variable containers, unsupported Java types, non-String map keys, nested containers as map values, and properties that generated code cannot populate are rejected with compiler diagnostics. The generated builder is in the model's package, so models referenced from other packages must be accessible there.

## Diagnosing common compile-time errors

| Diagnostic | Resolution |
|---|---|
| A non-object root requires `rootField` | Set `rootField`, or leave exactly one non-excluded field on the model for automatic selection. |
| `rootField` does not name a non-excluded model field | Check the field spelling and ensure it is not excluded. `rootField` is not valid for an `OBJECT` root. |
| A model needs an accessible no-argument constructor | Add an accessible no-argument constructor; nested model classes must be static and concrete. |
| No accessible setter or field assignment is available | Make the field accessible from the generated package or provide an accessible `setFieldName(...)` setter. |
| Map properties require `@PactMapMatcher` | Add the annotation and declare a concrete `Map<String, T>` type. |
| `INTEGER_TYPE` examples must fit in a 32-bit integer | Use `NUMBER_TYPE` or `AUTO` for a wider example. |
| `ARRAY_CONTAINS` is only supported for array properties of object roots | Keep containment on an object-root array property; root-array containment is unavailable in Pact 4.7.6. |
| Generated builder cannot be found | Confirm the compiler's annotation-processor path and check earlier compiler diagnostics for an invalid model or annotation. |

Two annotated models in the same package cannot generate builders with the same simple class name. Rare collisions between generated expected-instance helper names are also reported as compiler diagnostics.

## Tests and generated-source goldens

Run the processor test suite:

```shell
./mvnw -B test
```

On Windows PowerShell, run `.\mvnw.cmd -B test`.

Input models are checked-in under `src/test/resources/fixtures`; generated Java is compared with goldens under `src/test/resources/goldens`. Tests also compile and execute generated builders, inspect Pact output/matcher behavior, and assert expected-instance values. The test suite includes the examples above to prevent documentation snippets from drifting.

The independent Maven consumer verifies annotation-processor service discovery from the packaged artifact. Run it from the project root:

```shell
./mvnw -B install
./mvnw -B -f src/test/packaged-consumer/pom.xml clean compile
```

On Windows PowerShell, use `.\mvnw.cmd` in place of `./mvnw`. The same install and packaged-consumer checks run in GitHub Actions for pushes and pull requests.

To intentionally regenerate source snapshots after reviewing a processor change, run:

```shell
./mvnw -B '-Dpact.updateGoldens=true' test
```

On Windows PowerShell, run `.\mvnw.cmd -B '-Dpact.updateGoldens=true' test`.

Review the changed goldens, then rerun `./mvnw -B test` (or `.\mvnw.cmd -B test` on Windows) without the update property so the test suite verifies the checked-in outputs.

## Scope and current limits

The output is Pact JSON DSL only. XML, multipart, arbitrary binary payloads, Pact interaction/provider setup, and runtime Bean Validation are outside scope. `@PactMin` / `@PactMax` select or validate numeric examples but do not create Pact numeric range rules. Root scalar/array values do not support `INCLUDES` or `URL`; root-array `ARRAY_CONTAINS` is unavailable in the selected Pact 4.7.6 DSL API and fails with a compile-time diagnostic.
