package com.pact.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forSourceString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.testing.compile.Compilation;
import java.util.Map;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class PactDslProcessorValidationTest extends PactDslProcessorTestSupport {
    @Test
    void fieldAnnotationsDoNotTriggerBuilderGenerationWithoutBuilderModel() {
        JavaFileObject model = forSourceString("fixture.UnprocessedModel", """
                package fixture;

                import com.pact.processor.annotation.PactArrayMatcher;
                import com.pact.processor.annotation.PactExample;
                import com.pact.processor.annotation.PactExclude;
                import com.pact.processor.annotation.PactMapMatcher;
                import com.pact.processor.annotation.PactMatcher;
                import com.pact.processor.annotation.PactMax;
                import com.pact.processor.annotation.PactMin;
                import java.util.List;
                import java.util.Map;

                class UnprocessedModel {
                    @PactMatcher String matcher;
                    @PactExample("sample") String example;
                    @PactArrayMatcher List<String> values;
                    @PactMapMatcher Map<String, Integer> map;
                    @PactMin(1) @PactMax(5) int bounded;
                    @PactExclude String excluded;
                }
                """);

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertTrue(compilation.generatedSourceFile("fixture.UnprocessedModelBuilder").isEmpty());
    }

    @Test
    void reportsInvalidCustomTemporalExamplesAtCompileTime() {
        JavaFileObject model = fixture("fixture/InvalidTemporalExample.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Invalid example 'not-a-date'");

        JavaFileObject invalidFormat = fixture("fixture/InvalidTemporalFormat.java");
        Compilation invalidFormatCompilation = javac().withProcessors(new PactDslProcessor()).compile(invalidFormat);

        assertThat(invalidFormatCompilation).failed();
        assertThat(invalidFormatCompilation).hadErrorContaining("Invalid date/time format");

        Compilation invalidDate = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidDateExample.java"));
        assertThat(invalidDate).failed();
        assertThat(invalidDate).hadErrorContaining("Invalid example 'not-a-date' for java.util.Date");
    }

    @Test
    void reportsInvalidRegexConfigurationAtCompileTime() {
        JavaFileObject model = fixture("fixture/InvalidMatcher.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("requires a non-blank regex");
    }

    @Test
    void validatesMatcherArgumentsAndExamplesBeforeSourceGeneration() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidMatcherArguments.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Invalid Pact regex");
        assertThat(compilation).hadErrorContaining("does not match the configured regex");
        assertThat(compilation).hadErrorContaining("URL matcher example must match");
        assertThat(compilation).hadErrorContaining("URL matching requires at least one path fragment");
        assertThat(compilation).hadErrorContaining("URL matching requires a non-blank baseUrl");
        assertThat(compilation).hadErrorContaining("custom examples are not supported");
        assertThat(compilation).hadErrorContaining("UUID matcher examples must be valid UUID strings");
        assertThat(compilation).hadErrorContaining("HEX matcher examples must contain only hexadecimal characters");
        assertThat(compilation).hadErrorContaining("ID examples must be valid 64-bit integers");
        assertThat(compilation).hadErrorContaining("includes matching requires an example substring");
        assertThat(compilation).hadErrorContaining("regex argument can only be used with REGEX matching");
        assertThat(compilation).hadErrorContaining("URL arguments can only be used with URL matching");
        assertThat(compilation).hadErrorContaining("Matcher formats can only be used with supported date/time properties");
        assertThat(compilation).hadErrorContaining("Example formats can only be used with supported date/time properties");
    }

    @Test
    void rejectsIntegerMatcherExamplesThatCannotFitItsPactDslSignature() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidLargeIntegerMatcher.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("INTEGER_TYPE examples must fit in a 32-bit integer");
    }

    @Test
    void reportsConflictingMatcherAndExampleValues() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/ConflictingExamplesWithMatcher.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining(
                "@PactExample and @PactMatcher.example specify different values");
    }

    @Test
    void rejectsExamplesAndTypesThatCannotMatchTheirConfiguredMatcher() {
        Compilation typeCompilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidMatcherTypes.java"));
        assertThat(typeCompilation).failed();
        assertThat(typeCompilation).hadErrorContaining("IP_V4 matching requires a String property");
        assertThat(typeCompilation).hadErrorContaining("HEX matching requires a String or character property");
        assertThat(typeCompilation).hadErrorContaining("UUID matching requires a String or UUID property");

        Compilation nullCompilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidNullMatcherExample.java"));
        assertThat(nullCompilation).failed();
        assertThat(nullCompilation).hadErrorContaining("NULL_VALUE matching cannot specify an example");
        assertThat(nullCompilation).hadErrorContaining("A primitive property cannot use the NULL_VALUE matcher");
        assertThat(nullCompilation).hadErrorContaining("NULL_VALUE matching cannot specify a format");
        assertThat(nullCompilation).hadErrorContaining("NULL_VALUE matching cannot use @PactMin or @PactMax");
    }

    @Test
    void rejectsInvalidDefaultsAndNonFiniteNumericExamplesAtCompileTime() {
        Compilation invalidTemporal = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidTemporalDefaultFormat.java"));
        assertThat(invalidTemporal).failed();
        assertThat(invalidTemporal).hadErrorContaining("Invalid example '2024-01-02T03:04:05'");

        Compilation nonFinite = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/NonFiniteNumericExamples.java"));
        assertThat(nonFinite).failed();
        assertThat(nonFinite).hadErrorContaining("Numeric examples must be finite");

        Compilation inexactDecimal = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidDecimalBigInteger.java"));
        assertThat(inexactDecimal).failed();
        assertThat(inexactDecimal).hadErrorContaining(
                "DECIMAL_TYPE examples must be finite and exactly representable as a Pact double example");

        Compilation inexactLong = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidDecimalLong.java"));
        assertThat(inexactLong).failed();
        assertThat(inexactLong).hadErrorContaining(
                "DECIMAL_TYPE examples must be finite and exactly representable as a Pact double example");
    }

    @Test
    void rejectsImpossibleNumericAndArrayBoundsWithCompileTimeDiagnostics() {
        Compilation numeric = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidNumericBound.java"));
        assertThat(numeric).failed();
        assertThat(numeric).hadErrorContaining("does not fit the numeric property type");

        Compilation constrainedInteger = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidConstrainedIntegerMatcher.java"));
        assertThat(constrainedInteger).failed();
        assertThat(constrainedInteger).hadErrorContaining(
                "INTEGER_TYPE examples must fit in a 32-bit integer");

        Compilation arrays = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidArrayBounds.java"));
        assertThat(arrays).failed();
        assertThat(arrays).hadErrorContaining("selected array matcher requires the corresponding");
        assertThat(arrays).hadErrorContaining("minimum array size cannot exceed the maximum");
        assertThat(arrays).hadErrorContaining("Pact array bounds must be -1 (unspecified) or non-negative");
        assertThat(arrays).hadErrorContaining("@PactMin and @PactMax can only constrain numeric properties");
        assertThat(arrays).hadErrorContaining("Pact array minimum size cannot exceed 1000 generated examples");
    }

    @Test
    void rejectsInvalidNestedModelKindsAndEnumsWithoutConstants() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidNestedModelKinds.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Nested Pact model types must be concrete classes");
        assertThat(compilation).hadErrorContaining("Enum properties must declare at least one constant");
        assertThat(compilation).hadErrorContaining("Nested Pact model classes must be static");

        Compilation invalidRoots = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidBuilderKinds.java"));
        assertThat(invalidRoots).failed();
        assertThat(invalidRoots).hadErrorContaining("@PactDslBodyBuilder can only be used on a non-abstract class");
    }

    @Test
    void reportsInvalidRootFields() {
        Compilation invalidRoot = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidRootField.java"));
        assertThat(invalidRoot).failed();
        assertThat(invalidRoot).hadErrorContaining("rootField 'missing' does not name a non-excluded model field");

        Compilation objectRootField = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidObjectRootField.java"));
        assertThat(objectRootField).failed();
        assertThat(objectRootField).hadErrorContaining("rootField can only be set for non-object roots");

        Compilation ambiguousRoot = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidAutomaticRootFields.java"));
        assertThat(ambiguousRoot).failed();
        assertThat(ambiguousRoot).hadErrorContaining(
                "A non-object Pact root requires rootField to identify exactly one field");

        Compilation invalidRootShapes = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidRootShapes.java"));
        assertThat(invalidRootShapes).failed();
        assertThat(invalidRootShapes).hadErrorContaining("A VALUE root requires a scalar or enum rootField");
        assertThat(invalidRootShapes).hadErrorContaining("An ARRAY root requires an array or Collection rootField");
    }

    @Test
    void rejectsUnsupportedModelAndContainerShapes() {
        String[][] cases = {
            {"fixture/RawContainer.java", "Raw collections are not supported"},
            {"fixture/WildcardContainer.java", "Unsupported Pact JSON field type: ?"},
            {"fixture/GenericContainer.java", "Unsupported Pact JSON field type: T"},
            {"fixture/NestedMap.java", "Map values must be scalar or object values"},
            {"fixture/NoNoArgConstructor.java", "needs an accessible no-argument constructor"},
            {"fixture/NoWritableField.java", "No accessible setter or field assignment is available"}
        };
        for (String[] testCase : cases) {
            Compilation compilation = javac().withProcessors(new PactDslProcessor())
                    .compile(fixture(testCase[0]));
            assertThat(compilation).failed();
            assertThat(compilation).hadErrorContaining(testCase[1]);
        }

        Compilation collision = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/NameCollision.java"), fixture("fixture/NameCollisionBuilder.java"));
        assertThat(collision).failed();
        assertThat(collision).hadErrorContaining("Could not generate NameCollisionBuilder");
    }

    @Test
    void rejectsInvalidMapShapesAndRecursiveModels() {
        Compilation maps = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidMaps.java"));
        assertThat(maps).failed();
        assertThat(maps).hadErrorContaining("Maps must have a concrete String key type");
        assertThat(maps).hadErrorContaining("Map properties require @PactMapMatcher");
        assertThat(maps).hadErrorContaining("non-blank key example");
        assertThat(maps).hadErrorContaining("@PactMapMatcher can only be used on Map properties");
        assertThat(maps).hadErrorContaining("@PactMin and @PactMax can only constrain numeric properties");

        Compilation rawMap = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidRawMap.java"));
        assertThat(rawMap).failed();
        assertThat(rawMap).hadErrorContaining("concrete String key type and a concrete value type");

        Compilation recursive = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/RecursiveModel.java"));
        assertThat(recursive).failed();
        assertThat(recursive).hadErrorContaining("Recursive model reference detected");
    }

    @Test
    void rejectsModelPropertiesInaccessibleFromGeneratedPackage() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/ExternalModelReference.java"), fixture("other/PackagePrivateModel.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("needs an accessible no-argument constructor");
        assertThat(compilation).hadErrorContaining("No accessible setter or field assignment is available");
    }

    @Test
    void rejectsExpectedInstanceHelperNameCollisions() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(
                fixture("fixture/HelperNameCollision.java"),
                fixture("other/Outer.java"),
                fixture("other/Outer_Inner.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Generated expected-instance helper name collision");
    }

    @Test
    void reportsInvalidNumericExampleAndUnsupportedRootMatchers() throws Exception {
        JavaFileObject invalidNumeric = fixture("fixture/InvalidNumeric.java");
        Compilation numericCompilation = javac().withProcessors(new PactDslProcessor()).compile(invalidNumeric);
        assertThat(numericCompilation).failed();
        assertThat(numericCompilation).hadErrorContaining("below @PactMin(5)");

        Compilation invalidConstraints = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidNumericConstraints.java"));
        assertThat(invalidConstraints).failed();
        assertThat(invalidConstraints).hadErrorContaining("above @PactMax(5)");
        assertThat(invalidConstraints).hadErrorContaining("below @PactMin(5)");
        assertThat(invalidConstraints).hadErrorContaining("@PactMin cannot be greater than @PactMax");
        assertThat(invalidConstraints).hadErrorContaining("@PactMin and @PactMax can only constrain numeric properties");

        Compilation fractionalBound = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidFractionalBound.java"));
        assertThat(fractionalBound).failed();

        JavaFileObject rootUrl = fixture("fixture/RootUrl.java");
        Compilation rootCompilation = javac().withProcessors(new PactDslProcessor()).compile(rootUrl);
        assertThat(rootCompilation).failed();
        assertThat(rootCompilation).hadErrorContaining("not supported on a root JSON value");

        JavaFileObject invalidBoolean = fixture("fixture/InvalidBoolean.java");
        Compilation booleanCompilation = javac().withProcessors(new PactDslProcessor()).compile(invalidBoolean);
        assertThat(booleanCompilation).failed();
        assertThat(booleanCompilation).hadErrorContaining("expected true or false");

        JavaFileObject rootIncludes = fixture("fixture/RootIncludes.java");
        Compilation includesCompilation = javac().withProcessors(new PactDslProcessor()).compile(rootIncludes);
        assertThat(includesCompilation).failed();
        assertThat(includesCompilation).hadErrorContaining("not supported on a root JSON value");
    }
}
