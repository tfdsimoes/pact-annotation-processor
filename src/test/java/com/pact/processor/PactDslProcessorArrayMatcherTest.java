package com.pact.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.testing.compile.Compilation;
import au.com.dius.pact.consumer.dsl.DslPart;
import java.util.List;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class PactDslProcessorArrayMatcherTest extends PactDslProcessorTestSupport {
    @Test
    void generatesV4ArrayContainsForScalarAlternatives() throws Exception {
        JavaFileObject model = fixture("fixture/Choice.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.ChoiceBuilder");
        String generated = compilation.generatedSourceFile("fixture.ChoiceBuilder").orElseThrow()
                .getCharContent(true).toString();
        assertTrue(generated.contains("body.arrayContaining(\"colors\""));
        assertTrue(generated.contains("items.stringValue(\"red\")"));
        assertTrue(generated.contains("items.numberValue(2)"));
        assertTrue(generated.contains("items.booleanValue(true)"));
        assertTrue(generated.contains("items.stringValue(\"READY\")"));
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.ChoiceBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        assertTrue(body.getMatchers().isNotEmpty());
        assertTrue(hasMatcher(body, "ArrayContainsMatcher"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(List.of("red", "blue"), publicField(expected, "colors").get(expected));
        assertEquals(List.of(2, 7), publicField(expected, "quantities").get(expected));
        assertEquals(List.of(true, false), publicField(expected, "flags").get(expected));
        assertEquals(List.of("READY", "DONE"), ((List<?>) publicField(expected, "statuses").get(expected))
                .stream().map(Object::toString).toList());
    }

    @Test
    void rejectsInvalidV4ArrayContainsExamples() {
        JavaFileObject model = fixture("fixture/InvalidContains.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Invalid ARRAY_CONTAINS example");
    }

    @Test
    void preservesUniqueSetExamplesAndRejectsImpossibleSetExamples() throws Exception {
        Compilation valid = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/SetArrayContains.java"));
        assertThat(valid).succeeded();
        assertGeneratedSourceMatchesGolden(valid, "fixture.SetArrayContainsBuilder");
        Class<?> builderClass = loadCompiledClass(valid, "fixture.SetArrayContainsBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertTrue(hasMatcher(body, "ArrayContainsMatcher"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(java.util.Set.of("red", "blue"), publicField(expected, "colors").get(expected));

        Compilation invalid = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidSetMinimum.java"));
        assertThat(invalid).failed();
        assertThat(invalid).hadErrorContaining("Set properties cannot use a minimum size greater than one");
        assertThat(invalid).hadErrorContaining("ARRAY_CONTAINS examples for a Set must be unique");
    }

    @Test
    void generatesEmptyArraysForZeroMaximumBounds() throws Exception {
        Compilation objectCompilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/ZeroArrayBounds.java"));
        assertThat(objectCompilation).succeeded();
        assertGeneratedSourceMatchesGolden(objectCompilation, "fixture.ZeroArrayBoundsBuilder");
        Class<?> objectBuilderClass = loadCompiledClass(objectCompilation, "fixture.ZeroArrayBoundsBuilder");
        Object objectBuilder = objectBuilderClass.getConstructor().newInstance();
        DslPart objectBody = (DslPart) objectBuilderClass.getMethod("build").invoke(objectBuilder);
        assertNotNull(objectBody.getBody());
        assertTrue(objectBody.getMatchers().isNotEmpty());
        Object objectExpected = objectBuilderClass.getMethod("buildExpectedInstance").invoke(objectBuilder);
        assertEquals(List.of(), publicField(objectExpected, "emptyStrings").get(objectExpected));
        assertEquals(List.of(), publicField(objectExpected, "emptyNumbers").get(objectExpected));
        assertEquals(List.of(), publicField(objectExpected, "emptyObjects").get(objectExpected));
        assertEquals(0, ((int[][]) publicField(objectExpected, "emptyNestedArrays").get(objectExpected)).length);
        assertEquals(java.util.Set.of(), publicField(objectExpected, "emptySet").get(objectExpected));
        assertEquals(List.of(), publicField(objectExpected, "emptyUnordered").get(objectExpected));

        Compilation rootCompilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/ZeroRootArray.java"));
        assertThat(rootCompilation).succeeded();
        assertGeneratedSourceMatchesGolden(rootCompilation, "fixture.ZeroRootArrayBuilder");
        Class<?> rootBuilderClass = loadCompiledClass(rootCompilation, "fixture.ZeroRootArrayBuilder");
        Object rootBuilder = rootBuilderClass.getConstructor().newInstance();
        DslPart rootBody = (DslPart) rootBuilderClass.getMethod("build").invoke(rootBuilder);
        assertNotNull(rootBody.getBody());
        assertTrue(rootBody.getMatchers().isNotEmpty());
        Object rootExpected = rootBuilderClass.getMethod("buildExpectedInstance").invoke(rootBuilder);
        assertEquals(List.of(), publicField(rootExpected, "values").get(rootExpected));
    }

    @Test
    void rejectsInvalidArrayContainsModesAndAlternatives() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidArrayContainsModes.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("ARRAY_CONTAINS requires at least one contains example");
        assertThat(compilation).hadErrorContaining("Use either scalar contains examples or containsTypes");
        assertThat(compilation).hadErrorContaining("must not list the same class more than once");
        assertThat(compilation).hadErrorContaining("is not assignable to the array element type");
        assertThat(compilation).hadErrorContaining("currently supports String, character, boolean, numeric, or enum elements");
        assertThat(compilation).hadErrorContaining("ARRAY_CONTAINS cannot be combined with array size bounds");
        assertThat(compilation).hadErrorContaining("containsTypes entries must name accessible, concrete model classes");
        assertThat(compilation).hadErrorContaining("contains and containsTypes require the ARRAY_CONTAINS mode");

        Compilation unusedTypes = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/InvalidContainsTypesMode.java"));
        assertThat(unusedTypes).failed();
        assertThat(unusedTypes).hadErrorContaining("contains and containsTypes require the ARRAY_CONTAINS mode");
    }

    @Test
    void rejectsRootArrayContainsWhenPactDslDoesNotSupportIt() {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("fixture/RootArrayContains.java"));

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("ARRAY_CONTAINS is only supported for array properties of object roots");
    }

    @Test
    void mapsPactArraySizeBoundsToArrayRules() throws Exception {
        JavaFileObject model = fixture("fixture/ConstraintModels.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.ConstraintModelsBuilder");
        String generated = compilation.generatedSourceFile("fixture.ConstraintModelsBuilder").orElseThrow()
                .getCharContent(true).toString();
        assertTrue(generated.contains(".minArrayLike(\"minimum\", 2"));
        assertTrue(generated.contains(".minMaxArrayLike(\"bounded\", 3, 5"));
        assertTrue(generated.contains(".maxArrayLike(\"maximum\", 4"));
        assertTrue(generated.contains(".minArrayLike(\"arrayMinimum\", 2"));
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.ConstraintModelsBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(2, ((List<?>) publicField(expected, "minimum").get(expected)).size());
        assertEquals(3, ((List<?>) publicField(expected, "bounded").get(expected)).size());
        assertEquals(1, ((List<?>) publicField(expected, "maximum").get(expected)).size());
        assertEquals(2, ((String[]) publicField(expected, "arrayMinimum").get(expected)).length);
    }

    @Test
    void generatesAndExecutesEveryBoundedAndUnorderedArrayMode() throws Exception {
        JavaFileObject model = fixture("fixture/ArrayModes.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.ArrayModesBuilder");
        String generated = compilation.generatedSourceFile("fixture.ArrayModesBuilder").orElseThrow()
                .getCharContent(true).toString();
        assertTrue(generated.contains(".minArrayLike(\"minimum\""));
        assertTrue(generated.contains(".maxArrayLike(\"maximum\""));
        assertTrue(generated.contains(".minMaxArrayLike(\"bounded\""));
        assertTrue(generated.contains(".unorderedArray(\"unordered\""));
        assertTrue(generated.contains(".unorderedMinArray(\"unorderedMinimum\""));
        assertTrue(generated.contains(".unorderedMaxArray(\"unorderedMaximum\""));
        assertTrue(generated.contains(".unorderedMinMaxArray(\"unorderedBounded\""));
        assertTrue(generated.contains(".unorderedMinArray(\"nested\""));
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.ArrayModesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        assertTrue(body.getMatchers().isNotEmpty());
        assertTrue(hasMatcher(body, "MinTypeMatcher"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        int[][] nested = (int[][]) publicField(expected, "nested").get(expected);
        assertEquals(2, nested.length);
        assertEquals(1, nested[0].length);
    }

    @Test
    void generatesArrayContainsForPolymorphicObjectAlternatives() throws Exception {
        JavaFileObject model = fixture("fixture/PetVariants.java");

        Compilation compilation = javac().withProcessors(new PactDslProcessor()).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.PetVariantsBuilder");
        String generated = compilation.generatedSourceFile("fixture.PetVariantsBuilder").orElseThrow()
                .getCharContent(true).toString();
        assertTrue(generated.contains("items.object(contained"));
        assertTrue(generated.contains("createExpected_fixture_PetVariants_Cat()"));
        Class<?> builderClass = loadCompiledClass(compilation, "fixture.PetVariantsBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertTrue(hasMatcher(body, "ArrayContainsMatcher"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        List<?> pets = (List<?>) publicField(expected, "pets").get(expected);
        assertEquals(List.of("Cat", "Dog"), pets.stream().map(pet -> pet.getClass().getSimpleName()).toList());
    }
}
