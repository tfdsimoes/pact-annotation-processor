package com.pact.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import au.com.dius.pact.consumer.dsl.DslPart;
import com.google.testing.compile.Compilation;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.tools.JavaFileObject;
import static com.google.testing.compile.JavaFileObjects.forResource;

abstract class PactDslProcessorTestSupport {
    protected JavaFileObject fixture(String path) {
        return forResource("fixtures/" + path);
    }

    protected void assertGeneratedSourceMatchesGolden(Compilation compilation, String className) throws Exception {
        String actual = compilation.generatedSourceFile(className).orElseThrow()
                .getCharContent(true).toString().replace("\r\n", "\n");
        String resource = "goldens/" + className.replace('.', '/') + ".java";
        if (Boolean.getBoolean("pact.updateGoldens")) {
            Path output = Path.of("src", "test", "resources", resource);
            Files.createDirectories(output.getParent());
            Files.writeString(output, actual, StandardCharsets.UTF_8);
            return;
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input, "Missing generated-source golden: " + resource);
            String expected = new String(input.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
            assertEquals(expected, actual, "Generated source differs from " + resource);
        }
    }

    protected Field publicField(Object instance, String name) throws NoSuchFieldException {
        return instance.getClass().getField(name);
    }

    protected boolean hasMatcher(DslPart body, String matcherClassName) {
        return body.getMatchers().getMatchingRules().values().stream()
                .flatMap(group -> group.getRules().stream())
                .anyMatch(matcher -> matcher.getClass().getSimpleName().equals(matcherClassName));
    }

    protected Class<?> loadCompiledClass(Compilation compilation, String className) throws ClassNotFoundException {
        Map<String, JavaFileObject> classFiles = compilation.generatedFiles().stream()
                .filter(file -> file.getKind() == JavaFileObject.Kind.CLASS)
                .collect(java.util.stream.Collectors.toMap(
                        file -> file.toUri().getPath().replaceFirst("^/CLASS_OUTPUT/", "")
                                .replaceFirst("^/", "").replace("/", ".")
                                .replaceFirst("\\.class$", ""),
                        file -> file));
        ClassLoader classLoader = new ClassLoader(getClass().getClassLoader()) {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                JavaFileObject classFile = classFiles.get(name);
                if (classFile == null) {
                    throw new ClassNotFoundException(name + " not found in " + classFiles.keySet());
                }
                try (var input = classFile.openInputStream()) {
                    byte[] bytecode = input.readAllBytes();
                    return defineClass(name, bytecode, 0, bytecode.length);
                } catch (IOException exception) {
                    throw new ClassNotFoundException("Could not load generated class " + name, exception);
                }
            }
        };
        return classLoader.loadClass(className);
    }
}
