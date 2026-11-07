# Pact annotation processor implementation TODO

## Goal and boundaries

Build a Java annotation processor that generates Pact JVM `DslPart` JSON bodies and example model instances from model annotations. The supported baseline is Java 21 and Pact JVM consumer 4.7.6. Generation is compile-time only; interaction construction, provider verification, and test-framework integration stay in the consuming project.

The target is Pact's JSON body DSL, not every Pact feature or payload format. JSON object, array, string, number, boolean, and null shapes are in scope. XML, multipart, arbitrary binary payloads, and runtime Bean Validation are not. Pact V4 `arrayContaining` is supported for scalar, enum, and model-object alternatives on object-root array properties; root-array containment is rejected because Pact JVM consumer 4.7.6's `PactDslJsonArray.arrayContaining(String)` throws `UnsupportedOperationException` and the Lambda DSL exposes no root-array variant.

## Completed

- [x] Inventory the relevant Pact JVM 4.7.6 JSON DSL API and select the Java/Pact baseline.
- [x] Create project-owned annotations under `com.pact.processor.annotation`:
  - [x] `@PactDslBodyBuilder` with `OBJECT`, `ARRAY`, and `VALUE` roots.
  - [x] `@PactExample` with optional format metadata.
  - [x] `@PactMatcher` and `PactMatcherKind` for scalar matcher selection.
  - [x] `@PactArrayMatcher` and `PactArrayMode` for each-like, bounded, and unordered arrays.
  - [x] `@PactMapMatcher` for String-keyed dynamic maps.
  - [x] `@PactExclude`.
  - [x] `@PactMin` and `@PactMax` for numeric example bounds.
- [x] Use project-owned `@PactMin` / `@PactMax` for numeric example bounds and `@PactArrayMatcher` for array sizes without depending on Jakarta or Javax Validation APIs.
- [x] Configure Maven for Java 21, Pact DSL compilation, JUnit 5, compile-testing, and processor service registration.
- [x] Remove the starter `Main` class.
- [x] Generate `<ModelName>Builder` with `build()` and `buildExpectedInstance()`.
- [x] Generate object bodies, nested objects, scalar values, maps, collections, nested arrays, root scalar bodies, and root arrays for supported model shapes.
- [x] Generate Pact V4 `arrayContaining` for exact String, character, boolean, numeric, and enum alternatives on object-root array properties.
- [x] Generate Pact V4 `arrayContaining` for assignable model-object alternatives on object-root array properties.
- [x] Validate supported type boundaries, required matcher arguments, examples, regex configuration, map keys, array bounds, recursive graphs, and constructor/property population paths with compiler diagnostics.
- [x] Add an annotated compile-testing fixture that compiles and executes generated object, root-value, and root-array builders, then checks expected-instance values.
- [x] Move processor test models into checked-in Java fixtures and compare generated source against checked-in golden files.
- [x] Parse and generate `java.util.Date` examples consistently for date, time, and datetime matchers, with local values aligned to Pact's JVM-default-zone serialization.
- [x] Add an independent packaged Maven consumer fixture and verify service discovery and generated-source output against a golden file.
- [x] Test Pact array matcher categories and bounds, numeric example bounds, root matcher diagnostics, and Java service loading.
- [x] Parse custom `@PactExample(format = ...)` values consistently for supported `java.time` and `java.util.Date` types in generated DSL samples and expected instances.
- [x] Document setup, annotation semantics, examples, supported shapes, and current limits in `README.md`.

## Verification and coverage review

### Complete Pact JSON matcher coverage

- [x] Add an annotation representation and generation for Pact V4 scalar/enum `arrayContaining` alternatives; compile and execute generated builders against Pact 4.7.6.
- [x] Extend `ARRAY_CONTAINS` to model-object alternatives for object-root array properties; document root-array containment as unsupported.
- [x] Add negative tests for root `URL` and `INCLUDES` matchers, which produce explicit diagnostics rather than silently degrading the matcher.
- [x] Verify `UNORDERED` and bounded unordered matchers at runtime.
- [x] Audit supported scalar matcher examples and body/expected-instance parity, including temporal, URL/IP/UUID, null, enum, and numeric samples; keep unsupported root matcher combinations explicit.
- [x] Preserve wide integral examples without narrowing; use number matching when automatic integer samples exceed 32-bit range and reject incompatible explicit `INTEGER_TYPE` examples.
- [x] Verify root-array `ARRAY_CONTAINS` is not supported by Pact JVM consumer 4.7.6; reject it with a compile-time diagnostic and document the API limitation.
- [x] Update the README support matrix to reflect implemented capabilities and explicit limitations.

### Strengthen model and annotation validation

- [x] Add targeted validation tests for raw, wildcard, and type-variable containers; unsupported nested map shapes; inaccessible constructors/setters; generated-name collisions; and property visibility across package boundaries.
- [x] Add negative tests for conflicting example annotations and invalid root-field shapes.
- [x] Add negative tests for invalid temporal examples and malformed custom temporal formats.
- [x] Test invalid numeric examples, incompatible matcher/type pairs, and invalid containment examples.
- [x] Verify expected-instance values across supported scalars and matchers, including nulls, enums, dates/times, UUID, IP, URL, maps, sets, arrays, and nested collections.
- [x] Keep records and immutable models unsupported and explicitly documented for this phase.

### Expand integration coverage

- [x] Add focused processor fixtures for every `PactMatcherKind`.
- [x] Assert representative generated JSON examples and matcher categories from Pact's `DslPart`, in addition to generated-source text and compilation success.
- [x] Replace Jakarta and Javax constraints with project-owned numeric bounds and Pact array bounds; remove the validation API dependencies.
- [x] Compile a small consumer fixture using the packaged artifact to verify service discovery and Maven annotation-processor configuration end to end.
- [x] Add file-backed fixture/golden coverage for matcher and model-validation cases.
- [x] Run the complete Maven package lifecycle after the implementation changes.

### Close gaps found during the project-wide audit

- [x] Trace each public annotation member, matcher kind, array mode, root kind, supported model shape, validation rule, generated sample, and documented boundary to checked-in test evidence.
- [x] Compile and execute fixture/golden coverage for all supported root-value matcher kinds, all root-array matcher modes, and scalar/enum/object/nested-array root-array elements.
- [x] Verify current `@PactExample` / `@PactExclude` behavior after removing legacy aliases, along with explicit/automatic root-field selection, inherited fields, accessible setters, `Collection`, static/transient omission, and generated helper-name collisions.
- [x] Cover signed numeric bounds, 32-bit integer endpoints, wide positive/negative integral samples, exact decimal-double conversion, non-finite numeric rejection, and separation of numeric examples from collection-size constraints.
- [x] Handle numeric constraint type-use annotations on array elements, zero-sized maximum array examples, and Set uniqueness/minimum-size limits without runtime Pact DSL failures.
- [x] Cover default and custom temporal examples, invalid default formats, type-compatible character matchers, Java string/character escaping, invalid matcher arguments, map definitions, containment alternatives, and recursive/unsupported model kinds.
- [x] Move the packaged-consumer fixture out of the test-resource tree and keep its Maven build output under the root `target` directory.
- [x] Compile-test the README object, root-value, and root-array examples; expand the annotation reference, usage instructions, compatibility guidance, and verified limitations.

## Current supported matcher inventory

`PactMatcherKind` currently includes `AUTO`, `EQUALS`, `STRING_TYPE`, `NUMBER_TYPE`, `INTEGER_TYPE`, `DECIMAL_TYPE`, `BOOLEAN_TYPE`, `REGEX`, `DATE`, `TIME`, `DATETIME`, `IP_V4`, `ID`, `HEX`, `UUID`, `INCLUDES`, `URL`, and `NULL_VALUE`. Wide `long`/`BigInteger` examples are preserved using `NUMBER_TYPE` when needed; explicit `INTEGER_TYPE` samples outside the 32-bit range and inexact `DECIMAL_TYPE` conversions fail with compiler diagnostics.

`PactArrayMode` currently includes `EACH_LIKE`, `MIN_LIKE`, `MAX_LIKE`, `MIN_MAX_LIKE`, `UNORDERED`, `UNORDERED_MIN`, `UNORDERED_MAX`, `UNORDERED_MIN_MAX`, and scalar/enum/model-object `ARRAY_CONTAINS` for object-root array properties. Root arrays support the corresponding Pact root-array size and unordered factories, including zero-sized maximum examples, but not array containment. Generated minimum examples are capped at 1,000 per property; Set properties cannot use minimum sizes greater than one, and their scalar/enum containment examples must be unique. `@PactMin` and `@PactMax` constrain/select numeric examples; Pact does not receive a numeric range matcher from them.

## References

- Pact JVM consumer JSON DSL: https://docs.pact.io/implementation_guides/jvm/consumer
- Reference project: https://github.com/sngular/pact-annotation-processor
