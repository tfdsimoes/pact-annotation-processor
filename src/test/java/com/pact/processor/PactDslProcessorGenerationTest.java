package com.pact.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.testing.compile.Compilation;
import au.com.dius.pact.consumer.dsl.DslPart;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class PactDslProcessorGenerationTest extends PactDslProcessorTestSupport {
    private static final class DeprecatedAnnotationProcessor extends AbstractProcessor {
        private boolean invoked;

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return Set.of("java.lang.Deprecated");
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.RELEASE_21;
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            invoked |= annotations.stream()
                    .anyMatch(annotation -> annotation.getQualifiedName().contentEquals("java.lang.Deprecated"));
            return true;
        }
    }

    @Test
    void leavesUnrelatedAnnotationsAvailableToOtherProcessors() {
        DeprecatedAnnotationProcessor otherProcessor = new DeprecatedAnnotationProcessor();
        JavaFileObject model = com.google.testing.compile.JavaFileObjects.forSourceLines(
                "fixture.DeprecatedOrder",
                "package fixture;",
                "import com.pact.processor.annotation.PactDslBodyBuilder;",
                "@Deprecated",
                "@PactDslBodyBuilder",
                "public class DeprecatedOrder {}");

        Compilation compilation = javac().withProcessors(new PactDslProcessor(), otherProcessor)
                .withOptions("-Xlint:processing")
                .compile(model);

        assertThat(compilation).succeededWithoutWarnings();
        assertTrue(otherProcessor.invoked);
    }

    @Test
    void claimsPactAnnotationsWithoutProcessingWarnings() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .withOptions("-Xlint:processing")
                .compile(fixture("fixture/Order.java"));

        assertThat(compilation).succeededWithoutWarnings();
    }

    @Test
    void generatesDslForJsonShapesAndCompilesExpectedInstanceBuilder() throws Exception {
        JavaFileObject model = fixture("fixture/Order.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.OrderBuilder");
        JavaFileObject generated = compilation.generatedSourceFile("fixture.OrderBuilder").orElseThrow();
        String source = generated.getCharContent(true).toString();
        assertTrue(source.contains("body.stringType(\"status\", \"special\")"));
        assertTrue(source.contains("body.numberType(\"largeLong\", 5000000000L)"));
        assertTrue(source.contains("body.numberType(\"largeNumber\", new java.math.BigInteger(\"12345678901234567890\"))"));
        assertTrue(source.contains("body.stringValue(\"state\", \"NEW\")"));
        assertTrue(source.contains("body.stringValue(\"fixed\", \"locked\")"));
        assertTrue(source.contains("body.numberType(\"score\""));
        assertTrue(source.contains("body.integerType(\"count\""));
        assertTrue(source.contains("body.decimalType(\"ratio\""));
        assertTrue(source.contains("body.booleanType(\"enabled\", false)"));
        assertTrue(source.contains("body.id(\"numericId\""));
        assertTrue(source.contains("body.object(\"address\""));
        assertTrue(source.contains("body.minArrayLike(\"tags\", 2"));
        assertTrue(source.contains("body.object(\"quantities\""));
        assertTrue(source.contains("map3.eachValueLike(\"sku-1\""));
        assertTrue(source.contains("body.stringMatcher(\"reference\""));
        assertTrue(source.contains("body.ipV4Address(\"ip\")"));
        assertTrue(source.contains("body.nullValue(\"nullable\")"));
        assertTrue(source.contains("body.eachArrayLike(\"coordinates\""));
        assertTrue(source.contains("item3.integerType((long) 1L)"));
        assertTrue(source.contains("body.eachArrayLike(\"matrix\""));
        assertTrue(source.contains("body.minArrayLike(\"retries\", 2"));
        assertTrue(source.contains("buildExpectedInstance()"));

        Class<?> builderClass = loadCompiledClass(compilation, "fixture.OrderBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        assertTrue(body.getMatchers().isNotEmpty());
        assertTrue(hasMatcher(body, "RegexMatcher"));
        assertEquals(1, body.getBody().get("quantities").get("sku-1").asNumber().intValue());
        assertEquals(1, body.getBody().get("coordinates").asArray().get(0).asArray().get(0).asNumber().intValue());
        assertEquals(1, body.getBody().get("matrix").asArray().get(0).asArray().get(0).asNumber().intValue());
        assertEquals("example", body.getBody().get("metadata").get("id-1").get("city").asString());

        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals("example", publicField(expected, "id").get(expected));
        assertEquals(2, ((List<?>) publicField(expected, "tags").get(expected)).size());
        assertEquals(java.util.Set.of("example"), publicField(expected, "categories").get(expected));
        assertEquals(List.of(1), publicField(expected, "numbers").get(expected));
        assertEquals(2, ((List<?>) publicField(expected, "retries").get(expected)).size());
        assertEquals(Map.of("sku-1", 1), publicField(expected, "quantities").get(expected));
        assertEquals("special", publicField(expected, "status").get(expected));
        assertEquals("named", publicField(expected, "displayName").get(expected));
        assertEquals('x', publicField(expected, "initial").get(expected));
        assertEquals(new java.math.BigInteger("12345678901234567890"),
                publicField(expected, "largeNumber").get(expected));
        assertEquals(5000000000L, publicField(expected, "largeLong").get(expected));
        assertEquals(-5000000000L, publicField(expected, "largeNegativeLong").get(expected));
        assertEquals(new java.math.BigInteger("-12345678901234567890"),
                publicField(expected, "largeNegativeNumber").get(expected));
        assertEquals(2.5F, publicField(expected, "precision").get(expected));
        assertEquals((byte) 1, publicField(expected, "smallByte").get(expected));
        assertEquals((short) 1, publicField(expected, "smallShort").get(expected));
        assertEquals("NEW", publicField(expected, "state").get(expected).toString());
        assertEquals("locked", publicField(expected, "fixed").get(expected));
        assertEquals(new java.math.BigDecimal("2.5"), publicField(expected, "score").get(expected));
        assertEquals(3L, publicField(expected, "count").get(expected));
        assertEquals(1.25D, publicField(expected, "ratio").get(expected));
        assertEquals(false, publicField(expected, "enabled").get(expected));
        assertEquals(42, publicField(expected, "numericId").get(expected));
        assertEquals("AB-42", publicField(expected, "reference").get(expected));
        assertEquals("127.0.0.1", publicField(expected, "ip").get(expected));
        assertEquals("AB12", publicField(expected, "checksum").get(expected));
        assertEquals(java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"),
                publicField(expected, "externalId").get(expected));
        assertEquals("fragile", publicField(expected, "description").get(expected));
        assertEquals("https://example.test/orders", publicField(expected, "link").get(expected));
        assertNull(publicField(expected, "nullable").get(expected));
        assertEquals(java.time.LocalDate.of(2024, 1, 2), publicField(expected, "created").get(expected));
        assertEquals(java.time.LocalTime.of(12, 30, 45), publicField(expected, "started").get(expected));
        assertEquals(Instant.parse("2024-01-02T03:04:05Z"), publicField(expected, "updated").get(expected));
        Object address = publicField(expected, "address").get(expected);
        assertEquals("example", publicField(address, "city").get(address));
        assertEquals(1, publicField(address, "number").get(address));
        assertEquals(1, ((List<?>) publicField(expected, "addresses").get(expected)).size());
        Map<?, ?> metadata = (Map<?, ?>) publicField(expected, "metadata").get(expected);
        assertEquals(java.util.Set.of("id-1"), metadata.keySet());
        Object metadataAddress = metadata.get("id-1");
        assertEquals("example", publicField(metadataAddress, "city").get(metadataAddress));
        assertEquals(List.of(List.of(1)), publicField(expected, "matrix").get(expected));
    }

    @Test
    void supportsRootScalarAndArrayBodies() throws Exception {
        JavaFileObject model = fixture("fixture/RootBodies.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.ScalarBuilder");
        assertGeneratedSourceMatchesGolden(compilation, "fixture.ArrayBuilder");
        assertTrue(compilation.generatedSourceFile("fixture.ScalarBuilder").isPresent());
        Class<?> scalarBuilderClass = loadCompiledClass(compilation, "fixture.ScalarBuilder");
        Object scalarBuilder = scalarBuilderClass.getConstructor().newInstance();
        DslPart scalarBody = (DslPart) scalarBuilderClass.getMethod("build").invoke(scalarBuilder);
        assertNotNull(scalarBody.getBody());
        assertTrue(scalarBody.getBody().toString().contains("root-value"));
        assertTrue(!scalarBody.getBody().toString().contains("not-root"));
        String arrayBuilder = compilation.generatedSourceFile("fixture.ArrayBuilder").orElseThrow()
                .getCharContent(true).toString();
        assertTrue(arrayBuilder.contains("LambdaDsl.newJsonArrayMinLike(2"));
        Class<?> arrayBuilderClass = loadCompiledClass(compilation, "fixture.ArrayBuilder");
        Object arrayBuilderInstance = arrayBuilderClass.getConstructor().newInstance();
        DslPart arrayBody = (DslPart) arrayBuilderClass.getMethod("build").invoke(arrayBuilderInstance);
        assertNotNull(arrayBody.getBody());
        assertTrue(!arrayBody.getBody().toString().contains("not-root"));
        Object expectedArray = arrayBuilderClass.getMethod("buildExpectedInstance").invoke(arrayBuilderInstance);
        assertEquals(2, ((List<?>) publicField(expectedArray, "values").get(expectedArray)).size());
    }

    @Test
    void generatesEverySupportedRootArrayMode() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/RootArrayModes.java"));

        assertThat(compilation).succeeded();
        for (String builderName : List.of(
                "ArrayEachBuilder",
                "ArrayMinBuilder",
                "ArrayMaxBuilder",
                "ArrayMinMaxBuilder",
                "ArrayUnorderedBuilder",
                "ArrayUnorderedMinBuilder",
                "ArrayUnorderedMaxBuilder",
                "ArrayUnorderedMinMaxBuilder")) {
            String qualifiedName = "fixture." + builderName;
            assertGeneratedSourceMatchesGolden(compilation, qualifiedName);
            Class<?> builderClass = loadCompiledClass(compilation, qualifiedName);
            Object builder = builderClass.getConstructor().newInstance();
            DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
            assertNotNull(body.getBody());
            assertTrue(body.getMatchers().isNotEmpty());
            assertNotNull(builderClass.getMethod("buildExpectedInstance").invoke(builder));
        }
    }

    @Test
    void generatesRootArraysForScalarEnumObjectAndNestedArrayElements() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/RootArrayKinds.java"));

        assertThat(compilation).succeeded();
        for (String builderName : List.of(
                "ArrayStringsBuilder",
                "ArrayBooleansBuilder",
                "ArrayDecimalsBuilder",
                "ArrayDatesBuilder",
                "ArrayUuidsBuilder",
                "ArrayEnumsBuilder",
                "ArrayObjectsBuilder",
                "ArrayNestedBuilder")) {
            String qualifiedName = "fixture." + builderName;
            assertGeneratedSourceMatchesGolden(compilation, qualifiedName);
            Class<?> builderClass = loadCompiledClass(compilation, qualifiedName);
            Object builder = builderClass.getConstructor().newInstance();
            DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
            assertNotNull(body.getBody());
            if (builderName.equals("ArrayDatesBuilder")) {
                assertTrue(hasMatcher(body, "DateMatcher"));
            }
            Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
            assertNotNull(publicField(expected, "values").get(expected));
        }
    }

    @Test
    void generatesSupportedRootValueMatcherKinds() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/RootValueMatchers.java"));

        assertThat(compilation).succeeded();
        for (String builderName : List.of(
                "AutoBooleanBuilder",
                "AutoEnumBuilder",
                "AutoUuidBuilder",
                "EnumEqualsBuilder",
                "StringTypeBuilder",
                "NumberTypeBuilder",
                "IntegerTypeBuilder",
                "DecimalTypeBuilder",
                "BooleanTypeBuilder",
                "RegexBuilder",
                "DateBuilder",
                "TimeBuilder",
                "DateTimeBuilder",
                "IpV4Builder",
                "IdBuilder",
                "HexBuilder",
                "UuidBuilder",
                "NullValueBuilder")) {
            String qualifiedName = "fixture." + builderName;
            assertGeneratedSourceMatchesGolden(compilation, qualifiedName);
            Class<?> builderClass = loadCompiledClass(compilation, qualifiedName);
            Object builder = builderClass.getConstructor().newInstance();
            DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
            assertNotNull(body);
            Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
            assertNotNull(expected);
        }
    }

    @Test
    void parsesCustomTemporalExampleFormatsForDslAndExpectedInstance() throws Exception {
        JavaFileObject model = fixture("fixture/TemporalValues.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.TemporalValuesBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.TemporalValuesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        String bodyJson = body.getBody().toString();
        assertTrue(bodyJson.contains("02/01/2024"));
        assertTrue(bodyJson.contains("03:04 PM"), bodyJson);
        String instantExample = java.time.format.DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss XXX", Locale.ROOT)
                .withZone(java.time.ZoneOffset.UTC)
                .format(Instant.parse("2024-01-02T03:04:05Z"));
        assertTrue(bodyJson.contains(instantExample));

        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(java.time.LocalDate.of(2024, 1, 2), publicField(expected, "date").get(expected));
        assertEquals(java.time.LocalTime.of(15, 4), publicField(expected, "time").get(expected));
        assertEquals(java.time.LocalDateTime.of(2024, 1, 2, 3, 4, 5),
                publicField(expected, "datetime").get(expected));
        assertEquals(Instant.parse("2024-01-02T03:04:05Z"),
                publicField(expected, "instant").get(expected));
    }

    @Test
    void parsesDateExamplesUsingTheSelectedMatcherFormat() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/DateValues.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.DateValuesBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.DateValuesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        String bodyJson = body.getBody().toString();
        assertTrue(bodyJson.contains("02/01/2024"));
        assertTrue(bodyJson.contains("03:04 PM"), bodyJson);
        String datetimeExample = java.time.format.DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss XXX", Locale.ROOT)
                .withZone(java.time.ZoneOffset.UTC)
                .format(Instant.parse("2024-01-02T03:04:05Z"));
        assertTrue(bodyJson.contains(datetimeExample));

        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(java.util.Date.from(java.time.LocalDate.of(2024, 1, 2)
                        .atStartOfDay(java.time.ZoneOffset.UTC).toInstant()),
                publicField(expected, "date").get(expected));
        assertEquals(java.util.Date.from(java.time.LocalTime.of(15, 4)
                        .atDate(java.time.LocalDate.of(1970, 1, 1))
                        .atZone(java.time.ZoneOffset.UTC).toInstant()),
                publicField(expected, "time").get(expected));
        assertEquals(java.util.Date.from(Instant.parse("2024-01-02T03:04:05Z")),
                publicField(expected, "datetime").get(expected));
    }

    @Test
    void parsesExamplesWithRootLocaleAndUsesExplicitObjectBodyTimeZone() throws Exception {
        Locale originalLocale = Locale.getDefault();
        TimeZone originalTimeZone = TimeZone.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Honolulu"));

            Compilation compilation = javac().withProcessors(new PactDslProcessor())
                    .compile(fixture("fixture/TemporalLocaleZone.java"));

            assertThat(compilation).succeeded();
            Class<?> builderClass = loadCompiledClass(compilation, "fixture.TemporalLocaleZoneBuilder");
            Object builder = builderClass.getConstructor().newInstance();
            DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
            String bodyJson = body.getBody().toString();
            assertTrue(bodyJson.contains("02, 2024"), bodyJson);
            assertFalse(bodyJson.contains("Jan"), bodyJson);
            assertTrue(bodyJson.contains("03:04"), bodyJson);

            Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
            assertEquals(java.util.Date.from(java.time.LocalDate.of(2024, 1, 2)
                            .atStartOfDay(java.time.ZoneOffset.UTC).toInstant()),
                    publicField(expected, "date").get(expected));
            assertEquals(java.util.Date.from(java.time.LocalTime.of(15, 4)
                            .atDate(java.time.LocalDate.of(1970, 1, 1))
                            .atZone(java.time.ZoneOffset.UTC).toInstant()),
                    publicField(expected, "time").get(expected));
            assertEquals(java.util.Date.from(java.time.LocalDateTime.of(2024, 1, 2, 15, 4)
                            .atZone(java.time.ZoneOffset.UTC).toInstant()),
                    publicField(expected, "datetime").get(expected));
        } finally {
            Locale.setDefault(originalLocale);
            TimeZone.setDefault(originalTimeZone);
        }
    }

    @Test
    void generatesRootTemporalValuesWithTheirTemporalMatcher() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/RootTemporalValue.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.RootTemporalValueBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.RootTemporalValueBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertTrue(body.getBody().toString().contains("03:04 PM"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(java.time.LocalTime.of(15, 4), publicField(expected, "value").get(expected));
    }

    @Test
    void generatesRootTemporalValuesUsingTheDefaultTimeZone() throws Exception {
        Locale originalLocale = Locale.getDefault();
        TimeZone originalTimeZone = TimeZone.getDefault();
        try {
            Locale.setDefault(Locale.US);
            TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Honolulu"));

            assertRootTemporalBody("fixture/RootTemporalDate.java", "2024-01-02");
            assertRootTemporalBody("fixture/RootTemporalLocalDate.java", "2024-01-02");
            assertRootTemporalBody("fixture/RootTemporalValue.java", "03:04 PM");
            assertRootTemporalBody("fixture/RootTemporalLocalDateTime.java", "2024-01-02T15:04:05");
        } finally {
            Locale.setDefault(originalLocale);
            TimeZone.setDefault(originalTimeZone);
        }
    }

    private void assertRootTemporalBody(String fixturePath, String expectedExample) throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(fixture(fixturePath));
        assertThat(compilation).succeeded();
        String modelName = fixturePath.substring(fixturePath.lastIndexOf('/') + 1,
                fixturePath.length() - ".java".length());
        Class<?> builderClass = loadCompiledClass(compilation, "fixture." + modelName + "Builder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertTrue(body.getBody().toString().contains(expectedExample), body.getBody().toString());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        Object expectedValue = switch (modelName) {
            case "RootTemporalDate" -> java.util.Date.from(java.time.LocalDate.of(2024, 1, 2)
                    .atStartOfDay(java.time.ZoneOffset.UTC).toInstant());
            case "RootTemporalLocalDate" -> java.time.LocalDate.of(2024, 1, 2);
            case "RootTemporalValue" -> java.time.LocalTime.of(15, 4);
            case "RootTemporalLocalDateTime" -> java.time.LocalDateTime.of(2024, 1, 2, 15, 4, 5);
            default -> throw new AssertionError("Unexpected temporal fixture: " + modelName);
        };
        assertEquals(expectedValue, publicField(expected, "value").get(expected));
    }

    @Test
    void parsesDefaultTemporalExamplesForObjectBodies() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/TemporalDefaults.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.TemporalDefaultsBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.TemporalDefaultsBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(java.time.LocalDateTime.of(2024, 1, 2, 3, 4, 5),
                publicField(expected, "localDateTime").get(expected));
        assertEquals(java.time.LocalTime.of(3, 4, 5),
                publicField(expected, "localTime").get(expected));
        assertEquals(java.time.ZonedDateTime.of(
                        2024, 1, 2, 3, 4, 5, 0, java.time.ZoneOffset.UTC),
                publicField(expected, "zonedDateTime").get(expected));
        assertEquals(java.time.OffsetDateTime.of(
                        2024, 1, 2, 3, 4, 5, 0, java.time.ZoneOffset.ofHours(2)),
                publicField(expected, "offsetDateTime").get(expected));
    }

    @Test
    void generatesConfiguredExamplesAndExcludedFields() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/AnnotationExamples.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.AnnotationExamplesBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.AnnotationExamplesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        String bodyJson = body.getBody().toString();
        assertTrue(bodyJson.contains("configured"));
        assertTrue(bodyJson.contains("31/12/2024"));
        assertTrue(!bodyJson.contains("excluded"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(java.time.LocalDate.of(2024, 12, 31),
                publicField(expected, "formattedDate").get(expected));
        assertEquals("configured", publicField(expected, "text").get(expected));
        assertNull(publicField(expected, "excluded").get(expected));
        assertNull(publicField(expected, "alsoExcluded").get(expected));
    }

    @Test
    void preservesEscapedStringAndCharacterExamples() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/StringEscapes.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.StringEscapesBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.StringEscapesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals("quote: \" slash: \\ newline:\n tab:\t",
                publicField(expected, "text").get(expected));
        assertEquals('\b', publicField(expected, "backspace").get(expected));
        assertEquals('\f', publicField(expected, "formFeed").get(expected));
        assertEquals('\u0001', publicField(expected, "control").get(expected));
        assertEquals('\'', publicField(expected, "quote").get(expected));
        assertEquals('\\', publicField(expected, "backslash").get(expected));
        assertEquals('\n', publicField(expected, "newline").get(expected));
    }

    @Test
    void supportsPrivatePropertiesThroughAccessibleSetters() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/PrivateSetterModel.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.PrivateSetterModelBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.PrivateSetterModelBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertTrue(body.getBody().toString().contains("configured"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals("configured", expected.getClass().getMethod("getValue").invoke(expected));
    }

    @Test
    void includesInheritedInstanceFieldsAndIgnoresStaticAndTransientFields() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InheritedFields.java"), fixture("fixture/InheritedBase.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.InheritedFieldsBuilder");
        String generated = compilation.generatedSourceFile("fixture.InheritedFieldsBuilder").orElseThrow()
                .getCharContent(true).toString();
        assertTrue(generated.contains("\"inherited\""));
        assertTrue(generated.contains("\"child\""));
        assertTrue(!generated.contains("ignoredStatic"));
        assertTrue(!generated.contains("ignoredTransient"));
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.InheritedFieldsBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals("example", publicField(expected, "inherited").get(expected));
        assertEquals("example", publicField(expected, "child").get(expected));
    }

    @Test
    void generatesTypeCompatibleCharacterMatcherExamples() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/MatcherCharacterExamples.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.MatcherCharacterExamplesBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.MatcherCharacterExamplesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals('x', publicField(expected, "regexCharacter").get(expected));
        assertEquals('A', publicField(expected, "hexadecimalCharacter").get(expected));
        assertEquals('x', publicField(expected, "includedCharacter").get(expected));
    }

    @Test
    void generatesNumericExamplesWithinSignedConstraintsAndSeparatesArrayBounds() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/NumericBoundaries.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.NumericBoundariesBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.NumericBoundariesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(-2, publicField(expected, "negativeRange").get(expected));
        assertEquals(-1, publicField(expected, "maxMinusOne").get(expected));
        assertEquals(5, publicField(expected, "positiveRange").get(expected));
        assertEquals(List.of(1, 1, 1), publicField(expected, "values").get(expected));
        assertEquals(Integer.MIN_VALUE, publicField(expected, "minimumInteger").get(expected));
        assertEquals(Integer.MAX_VALUE, publicField(expected, "maximumInteger").get(expected));
    }

    @Test
    void preservesNumericMatcherExamplesAcrossJavaNumberTypes() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/NumericMatcherParity.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.NumericMatcherParityBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.NumericMatcherParityBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(Float.parseFloat("0.1000000001"),
                publicField(expected, "numberFloat").get(expected));
        assertEquals(5.0F, publicField(expected, "suffixedFloat").get(expected));
        assertEquals(new java.math.BigDecimal("0.1"),
                publicField(expected, "decimalValue").get(expected));
        assertEquals(new java.math.BigInteger("9007199254740992"),
                publicField(expected, "exactInteger").get(expected));
        assertEquals(Long.MIN_VALUE, publicField(expected, "minimumId").get(expected));
        assertEquals(Long.MAX_VALUE, publicField(expected, "maximumId").get(expected));
    }

    @Test
    void preservesWideIntegralExamplesInRootNumberBodies() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/WideRootNumber.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.WideRootNumberBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.WideRootNumberBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(new java.math.BigInteger("12345678901234567890"),
                publicField(expected, "value").get(expected));
    }
}
