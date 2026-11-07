package com.pact.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.testing.compile.Compilation;
import au.com.dius.pact.consumer.dsl.DslPart;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class PactDslProcessorIntegrationTest extends PactDslProcessorTestSupport {
    @Test
    void compilesTheDocumentedOrderExample() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(
                        fixture("docs/Order.java"),
                        fixture("docs/ScalarBody.java"),
                        fixture("docs/ArrayBody.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "example.OrderBuilder");
        assertGeneratedSourceMatchesGolden(compilation, "example.ScalarBodyBuilder");
        assertGeneratedSourceMatchesGolden(compilation, "example.ArrayBodyBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "example.OrderBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals("ORD-42", publicField(expected, "id").get(expected));
        assertEquals(List.of("example"), publicField(expected, "tags").get(expected));

        Class<?> scalarBuilderClass = loadCompiledClass(compilation, "example.ScalarBodyBuilder");
        Object scalarBuilder = scalarBuilderClass.getConstructor().newInstance();
        assertNotNull(scalarBuilderClass.getMethod("build").invoke(scalarBuilder));
        Object scalarExpected = scalarBuilderClass.getMethod("buildExpectedInstance").invoke(scalarBuilder);
        assertEquals("accepted", publicField(scalarExpected, "value").get(scalarExpected));

        Class<?> arrayBuilderClass = loadCompiledClass(compilation, "example.ArrayBodyBuilder");
        Object arrayBuilder = arrayBuilderClass.getConstructor().newInstance();
        DslPart arrayBody = (DslPart) arrayBuilderClass.getMethod("build").invoke(arrayBuilder);
        assertNotNull(arrayBody.getBody());
        Object arrayExpected = arrayBuilderClass.getMethod("buildExpectedInstance").invoke(arrayBuilder);
        assertEquals(2, ((List<?>) publicField(arrayExpected, "orders").get(arrayExpected)).size());
    }

    @Test
    void compilesDocumentedMapTemporalConstraintAndExclusionExamples() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("docs/AdvancedOrder.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "example.AdvancedOrderBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "example.AdvancedOrderBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertNotNull(body.getBody());
        assertEquals(1, body.getBody().get("quantities").get("sku-1").asNumber().intValue());
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        assertEquals(Map.of("sku-1", 1), publicField(expected, "quantities").get(expected));
        assertEquals(java.time.LocalDate.of(2024, 1, 2), publicField(expected, "created").get(expected));
        assertEquals(1, publicField(expected, "adjustment").get(expected));
        assertEquals(List.of("example", "example"), publicField(expected, "tags").get(expected));
        assertNull(publicField(expected, "internalOnly").get(expected));
    }

    @Test
    void compilesDocumentedModelContainmentAlternatives() throws Exception {
        Compilation compilation = javac().withProcessors(new PactDslProcessor())
                .compile(fixture("docs/PetChoices.java"));

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "example.PetChoicesBuilder");
        Class<?> builderClass = loadCompiledClass(compilation, "example.PetChoicesBuilder");
        Object builder = builderClass.getConstructor().newInstance();
        DslPart body = (DslPart) builderClass.getMethod("build").invoke(builder);
        assertTrue(hasMatcher(body, "ArrayContainsMatcher"));
        Object expected = builderClass.getMethod("buildExpectedInstance").invoke(builder);
        List<?> pets = (List<?>) publicField(expected, "pets").get(expected);
        assertEquals(List.of("Cat", "Dog"),
                pets.stream().map(pet -> pet.getClass().getSimpleName()).toList());
    }

    @Test
    void discoversProcessorThroughServiceRegistration() throws Exception {
        JavaFileObject model = fixture("fixture/ServiceLoaded.java");

        Processor processor = ServiceLoader.load(Processor.class).findFirst().orElseThrow();
        assertTrue(processor instanceof PactDslProcessor);
        Compilation compilation = javac().withProcessors(processor).compile(model);

        assertThat(compilation).succeeded();
        assertGeneratedSourceMatchesGolden(compilation, "fixture.ServiceLoadedBuilder");
        assertTrue(compilation.generatedSourceFile("fixture.ServiceLoadedBuilder").isPresent());
    }
}
