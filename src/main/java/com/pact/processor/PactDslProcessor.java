package com.pact.processor;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactDslBodyBuilder;
import com.pact.processor.annotation.PactExclude;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/** Annotation processor that generates Pact JSON body builders for {@code @PactDslBodyBuilder} models. */
public final class PactDslProcessor extends AbstractProcessor {
    private static final String BUILDER_ANNOTATION =
            "com.pact.processor.annotation.PactDslBodyBuilder";
    private static final String PACT_DSL_PACKAGE = "au.com.dius.pact.consumer.dsl.";

    private final Set<String> generatedTypes = new HashSet<>();
    private PactDslModelValidator modelValidator;
    private PactDslSourceGenerator sourceGenerator;
    private PactDslValueResolver valueResolver;

    /** Creates a processor instance for Java's annotation-processing service. */
    public PactDslProcessor() {}

    @Override
    public synchronized void init(javax.annotation.processing.ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        modelValidator = new PactDslModelValidator(this);
        sourceGenerator = new PactDslSourceGenerator(this);
        valueResolver = new PactDslValueResolver(this);
    }

    javax.annotation.processing.ProcessingEnvironment processingEnvironment() {
        return processingEnv;
    }

    boolean fitsIntegerTypeExample(String value) {
        return modelValidator.fitsIntegerTypeExample(value);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
                BUILDER_ANNOTATION,
                "com.pact.processor.annotation.PactArrayMatcher",
                "com.pact.processor.annotation.PactExample",
                "com.pact.processor.annotation.PactExclude",
                "com.pact.processor.annotation.PactMapMatcher",
                "com.pact.processor.annotation.PactMatcher",
                "com.pact.processor.annotation.PactMax",
                "com.pact.processor.annotation.PactMin");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_21;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(PactDslBodyBuilder.class)) {
            if (element instanceof TypeElement type) {
                generateBuilder(type);
            }
        }
        return true;
    }

    private void generateBuilder(TypeElement model) {
        String qualifiedName = model.getQualifiedName().toString();
        if (!generatedTypes.add(qualifiedName)) {
            return;
        }

        if (model.getKind() != ElementKind.CLASS || model.getModifiers().contains(Modifier.ABSTRACT)) {
            error(model, "@PactDslBodyBuilder can only be used on a non-abstract class.");
            return;
        }

        PactDslBodyBuilder annotation = model.getAnnotation(PactDslBodyBuilder.class);
        List<VariableElement> fields = fieldsOf(model);
        List<VariableElement> bodyFields = fields.stream().filter(field -> !isExcluded(field)).toList();
        String rootField = annotation.rootField();
        if (annotation.root() == PactRootType.OBJECT && !rootField.isBlank()) {
            error(model, "rootField can only be set for non-object roots.");
            return;
        }
        if (annotation.root() != PactRootType.OBJECT && rootField.isBlank()) {
            if (bodyFields.size() != 1) {
                error(model, "A non-object Pact root requires rootField to identify exactly one field.");
                return;
            }
            rootField = bodyFields.get(0).getSimpleName().toString();
        }
        String selectedRootField = rootField;
        if (annotation.root() != PactRootType.OBJECT
                && bodyFields.stream().noneMatch(field -> field.getSimpleName().contentEquals(selectedRootField))) {
            error(model, "rootField '" + rootField + "' does not name a non-excluded model field.");
            return;
        }
        if (annotation.root() == PactRootType.VALUE && bodyFields.stream()
                .filter(field -> field.getSimpleName().contentEquals(selectedRootField))
                .anyMatch(field -> !hasScalarType(field.asType()) && !isEnum(field.asType()))) {
            error(model, "A VALUE root requires a scalar or enum rootField.");
            return;
        }
        if (annotation.root() == PactRootType.ARRAY && bodyFields.stream()
                .filter(field -> field.getSimpleName().contentEquals(selectedRootField))
                .anyMatch(field -> field.asType().getKind() != TypeKind.ARRAY && !isCollection(field.asType()))) {
            error(model, "An ARRAY root requires an array or Collection rootField.");
            return;
        }
        if (annotation.root() != PactRootType.OBJECT) {
            VariableElement rootValueField = findField(bodyFields, selectedRootField);
            PactMatcher matcher = rootValueField.getAnnotation(PactMatcher.class);
            if (matcher != null && (matcher.value() == PactMatcherKind.INCLUDES
                    || matcher.value() == PactMatcherKind.URL)) {
                error(rootValueField, matcher.value() + " matching is not supported on a root JSON value.");
                return;
            }
            if (annotation.root() == PactRootType.ARRAY
                    && effectiveArrayMode(rootValueField) == PactArrayMode.ARRAY_CONTAINS) {
                error(rootValueField, "ARRAY_CONTAINS is only supported for array properties of object roots.");
                return;
            }
        }

        Set<String> visiting = new LinkedHashSet<>();
        Map<String, TypeElement> modelTypes = new LinkedHashMap<>();
        if (!modelValidator.validateModel(model, visiting, modelTypes, packageName(model))) {
            return;
        }
        if (!modelValidator.validateExpectedHelperNames(model, modelTypes)) {
            return;
        }

        String packageName = packageName(model);
        String modelName = model.getSimpleName().toString();
        String builderName = modelName + "Builder";
        String source = sourceGenerator.createSource(packageName, model, fields, bodyFields, builderName, annotation.root(), rootField,
                modelTypes);

        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(
                    packageName.isEmpty() ? builderName : packageName + "." + builderName, model);
            try (var writer = file.openWriter()) {
                writer.write(source);
            }
        } catch (IOException exception) {
            error(model, "Could not generate " + builderName + ": " + exception.getMessage());
        }
    }

    String unorderedArrayDsl(
            String variable, String name, PactArrayMode mode, int minimum, int maximum, String value) {
        String method = switch (mode) {
            case UNORDERED_MIN -> "unorderedMinArray(" + name + ", " + minimum + ", ";
            case UNORDERED_MAX -> "unorderedMaxArray(" + name + ", " + maximum + ", ";
            case UNORDERED_MIN_MAX -> "unorderedMinMaxArray(" + name + ", " + minimum + ", " + maximum + ", ";
            default -> "unorderedArray(" + name + ", ";
        };
        return variable + "." + method + "array -> array.eachLike(" + value + "))";
    }

    PactArrayMode effectiveArrayMode(VariableElement field) {
        PactArrayMatcher matcher = field.getAnnotation(PactArrayMatcher.class);
        boolean hasMinimum = matcher != null && matcher.minSize() >= 0;
        boolean hasMaximum = matcher != null && matcher.maxSize() >= 0;
        PactArrayMode requested = matcher == null ? PactArrayMode.EACH_LIKE : matcher.value();
        return switch (requested) {
            case EACH_LIKE -> hasMinimum
                    ? hasMaximum ? PactArrayMode.MIN_MAX_LIKE : PactArrayMode.MIN_LIKE
                    : hasMaximum ? PactArrayMode.MAX_LIKE : PactArrayMode.EACH_LIKE;
            case MIN_LIKE -> hasMaximum ? PactArrayMode.MIN_MAX_LIKE : PactArrayMode.MIN_LIKE;
            case MAX_LIKE -> hasMinimum ? PactArrayMode.MIN_MAX_LIKE : PactArrayMode.MAX_LIKE;
            case MIN_MAX_LIKE -> PactArrayMode.MIN_MAX_LIKE;
            case UNORDERED -> hasMinimum
                    ? hasMaximum ? PactArrayMode.UNORDERED_MIN_MAX : PactArrayMode.UNORDERED_MIN
                    : hasMaximum ? PactArrayMode.UNORDERED_MAX : PactArrayMode.UNORDERED;
            case UNORDERED_MIN -> hasMaximum ? PactArrayMode.UNORDERED_MIN_MAX : PactArrayMode.UNORDERED_MIN;
            case UNORDERED_MAX -> hasMinimum ? PactArrayMode.UNORDERED_MIN_MAX : PactArrayMode.UNORDERED_MAX;
            case UNORDERED_MIN_MAX -> PactArrayMode.UNORDERED_MIN_MAX;
            case ARRAY_CONTAINS -> PactArrayMode.ARRAY_CONTAINS;
        };
    }

    int effectiveBound(VariableElement field, boolean minimum) {
        PactArrayMatcher matcher = field.getAnnotation(PactArrayMatcher.class);
        int explicit = matcher == null ? -1 : minimum ? matcher.minSize() : matcher.maxSize();
        return explicit < 0 ? 1 : explicit;
    }

    boolean hasNoArgConstructor(TypeElement model, String generatedPackage) {
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(model.getEnclosedElements());
        if (constructors.isEmpty()) {
            return isTypeAccessibleFromPackage(model, generatedPackage);
        }
        return constructors.stream().anyMatch(constructor -> constructor.getParameters().isEmpty()
                && isMemberAccessibleFromPackage(
                        constructor.getModifiers(), constructor.getEnclosingElement(), generatedPackage));
    }

    boolean hasWritablePath(TypeElement model, VariableElement field, String generatedPackage) {
        if (isMemberAccessibleFromPackage(field.getModifiers(), field.getEnclosingElement(), generatedPackage)) {
            return true;
        }
        return findSetter(model, field, generatedPackage) != null;
    }

    ExecutableElement findSetter(TypeElement model, VariableElement field, String generatedPackage) {
        String fieldName = field.getSimpleName().toString();
        String expectedName = "set" + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1);
        for (ExecutableElement method : ElementFilter.methodsIn(processingEnv.getElementUtils().getAllMembers(model))) {
            if (method.getSimpleName().contentEquals(expectedName)
                    && method.getParameters().size() == 1
                    && processingEnv.getTypeUtils().isAssignable(
                            field.asType(), method.getParameters().get(0).asType())
                    && isMemberAccessibleFromPackage(
                            method.getModifiers(), method.getEnclosingElement(), generatedPackage)
                    && !method.getModifiers().contains(Modifier.STATIC)) {
                return method;
            }
        }
        return null;
    }

    boolean isTypeAccessibleFromPackage(TypeElement type, String targetPackage) {
        Element current = type;
        while (current instanceof TypeElement currentType) {
            if (!isMemberAccessibleFromPackage(
                    currentType.getModifiers(), currentType.getEnclosingElement(), targetPackage)) {
                return false;
            }
            current = currentType.getEnclosingElement();
        }
        return true;
    }

    private boolean isMemberAccessibleFromPackage(
            Set<Modifier> modifiers, Element declaringElement, String targetPackage) {
        if (modifiers.contains(Modifier.PUBLIC)) {
            return true;
        }
        if (modifiers.contains(Modifier.PRIVATE)) {
            return false;
        }
        String declaringPackage = processingEnv.getElementUtils()
                .getPackageOf(declaringElement).getQualifiedName().toString();
        return declaringPackage.equals(targetPackage);
    }

    List<VariableElement> fieldsOf(TypeElement type) {
        Map<String, VariableElement> fields = new LinkedHashMap<>();
        TypeElement current = type;
        List<TypeElement> hierarchy = new ArrayList<>();
        while (current != null && !current.getQualifiedName().contentEquals("java.lang.Object")) {
            hierarchy.add(0, current);
            TypeMirror superclass = current.getSuperclass();
            if (superclass.getKind() != TypeKind.DECLARED) {
                break;
            }
            current = (TypeElement) ((DeclaredType) superclass).asElement();
        }
        for (TypeElement owner : hierarchy) {
            for (VariableElement field : ElementFilter.fieldsIn(owner.getEnclosedElements())) {
                Set<Modifier> modifiers = field.getModifiers();
                if (!modifiers.contains(Modifier.STATIC) && !modifiers.contains(Modifier.TRANSIENT)) {
                    fields.put(field.getSimpleName().toString(), field);
                }
            }
        }
        return List.copyOf(fields.values());
    }

    BigDecimal numericValueAsBigDecimal(TypeMirror type, String value) {
        return valueResolver.numericValueAsBigDecimal(type, value);
    }
    String normalizedArrayContainsExample(TypeMirror type, String example) {
        return valueResolver.normalizedArrayContainsExample(type, example);
    }
    String temporalDateExpression(TypeMirror type, String value, VariableElement field) {
        return valueResolver.temporalDateExpression(type, value, field);
    }

    String rootTemporalDateExpression(TypeMirror type, String value, VariableElement field) {
        return valueResolver.rootTemporalDateExpression(type, value, field);
    }
    String temporalParseExpression(TypeMirror type, String value, VariableElement field) {
        return valueResolver.temporalParseExpression(type, value, field);
    }
    java.time.format.DateTimeFormatter temporalFormatter(VariableElement field, TypeMirror type) {
        return valueResolver.temporalFormatter(field, type);
    }
    PactMatcherKind temporalMatcherKind(TypeMirror type, VariableElement field) {
        return valueResolver.temporalMatcherKind(type, field);
    }
    void validateDateExample(String example, VariableElement field) {
        valueResolver.validateDateExample(example, field);
    }
    String scalarDefaultValue(TypeMirror type, VariableElement field) {
        return valueResolver.scalarDefaultValue(type, field);
    }
    String urlExample(PactMatcher matcher) {
        return valueResolver.urlExample(matcher);
    }
    String explicitExample(VariableElement field) {
        return valueResolver.explicitExample(field);
    }
    String dateFormat(VariableElement field) {
        return valueResolver.dateFormat(field);
    }
    String dateTimeFormat(VariableElement field, PactMatcherKind kind) {
        return valueResolver.dateTimeFormat(field, kind);
    }
    Long minimumValue(VariableElement field) {
        return valueResolver.minimumValue(field);
    }
    Long maximumValue(VariableElement field) {
        return valueResolver.maximumValue(field);
    }
    String numericExample(TypeMirror type, String value) {
        return valueResolver.numericExample(type, value);
    }
    String intExample(String value) {
        return valueResolver.intExample(value);
    }
    String longExample(String value) {
        return valueResolver.longExample(value);
    }
    String doubleExample(TypeMirror type, String value) {
        return valueResolver.doubleExample(type, value);
    }
    boolean exactlyRepresentableAsDouble(TypeMirror type, String value) {
        return valueResolver.exactlyRepresentableAsDouble(type, value);
    }
    String parsePrimitiveValue(TypeMirror type, String value) {
        return valueResolver.parsePrimitiveValue(type, value);
    }

    boolean isExcluded(VariableElement field) {
        return field.getAnnotation(PactExclude.class) != null;
    }

    TypeMirror typeArgument(TypeMirror type, int index) {
        if (!(type instanceof DeclaredType declared) || declared.getTypeArguments().size() <= index) {
            return null;
        }
        return declared.getTypeArguments().get(index);
    }

    boolean isCollection(TypeMirror type) {
        if (!(type instanceof DeclaredType declared)) {
            return false;
        }
        String name = ((TypeElement) declared.asElement()).getQualifiedName().toString();
        return name.equals("java.util.List") || name.equals("java.util.Collection") || name.equals("java.util.Set");
    }

    boolean isSet(TypeMirror type) {
        return type instanceof DeclaredType declared
                && ((TypeElement) declared.asElement()).getQualifiedName().contentEquals("java.util.Set");
    }

    boolean isMap(TypeMirror type) {
        return type instanceof DeclaredType declared
                && ((TypeElement) declared.asElement()).getQualifiedName().contentEquals("java.util.Map");
    }

    boolean isNestedArray(TypeMirror type) {
        return isArrayLike(type);
    }

    boolean isArrayLike(TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY || isCollection(type);
    }

    TypeMirror arrayElementType(TypeMirror type) {
        return type.getKind() == TypeKind.ARRAY
                ? ((ArrayType) type).getComponentType()
                : typeArgument(type, 0);
    }

    boolean isScalarArrayElement(TypeMirror type) {
        return hasScalarType(type) || isEnum(type);
    }

    String typeName(TypeMirror type) {
        if (type instanceof DeclaredType declared && declared.asElement() instanceof TypeElement element) {
            return element.getQualifiedName().toString();
        }
        return type.toString();
    }

    boolean hasScalarType(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return true;
        }
        String name = typeName(type);
        return name.equals("java.lang.String") || name.equals("java.lang.Character")
                || name.equals("java.lang.Boolean") || isBoxedNumeric(type)
                || name.equals("java.math.BigDecimal") || name.equals("java.math.BigInteger")
                || name.equals("java.util.Date") || name.equals("java.time.LocalDate")
                || name.equals("java.time.LocalTime") || name.equals("java.time.LocalDateTime")
                || name.equals("java.time.OffsetDateTime") || name.equals("java.time.ZonedDateTime")
                || name.equals("java.time.Instant") || name.equals("java.util.UUID");
    }

    boolean isBoxedNumeric(TypeMirror type) {
        return Set.of("java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long",
                        "java.lang.Float", "java.lang.Double")
                .contains(typeName(type));
    }

    boolean isNumericType(TypeMirror type) {
        TypeKind kind = type.getKind();
        boolean primitiveNumeric = kind == TypeKind.BYTE || kind == TypeKind.SHORT || kind == TypeKind.INT
                || kind == TypeKind.LONG || kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE;
        return primitiveNumeric || isBoxedNumeric(type)
                || typeName(type).equals("java.math.BigDecimal")
                || typeName(type).equals("java.math.BigInteger");
    }

    boolean isIntegralType(TypeMirror type) {
        TypeKind kind = type.getKind();
        return kind == TypeKind.BYTE || kind == TypeKind.SHORT || kind == TypeKind.INT || kind == TypeKind.LONG
                || Set.of("java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long",
                                "java.math.BigInteger")
                        .contains(typeName(type));
    }

    boolean isDecimalType(TypeMirror type) {
        return type.getKind() == TypeKind.FLOAT || type.getKind() == TypeKind.DOUBLE
                || typeName(type).equals("java.lang.Float") || typeName(type).equals("java.lang.Double")
                || typeName(type).equals("java.math.BigDecimal");
    }

    boolean isStringType(TypeMirror type) {
        return type.getKind() == TypeKind.CHAR || typeName(type).equals("java.lang.String")
                || typeName(type).equals("java.lang.Character");
    }

    boolean isBooleanType(TypeMirror type) {
        return type.getKind() == TypeKind.BOOLEAN || typeName(type).equals("java.lang.Boolean");
    }

    boolean isDateType(TypeMirror type) {
        return Set.of("java.util.Date", "java.time.LocalDate", "java.time.LocalTime",
                        "java.time.LocalDateTime", "java.time.OffsetDateTime", "java.time.ZonedDateTime",
                        "java.time.Instant")
                .contains(typeName(type));
    }

    boolean isUuid(TypeMirror type) {
        return typeName(type).equals("java.util.UUID");
    }

    boolean isEnum(TypeMirror type) {
        if (type.getKind() != TypeKind.DECLARED) {
            return false;
        }
        return ((DeclaredType) type).asElement().getKind() == ElementKind.ENUM;
    }

    boolean isNestedModel(TypeMirror type) {
        return type.getKind() == TypeKind.DECLARED && !hasScalarType(type) && !isEnum(type)
                && !isCollection(type) && !isMap(type);
    }

    String firstEnumConstant(TypeMirror type) {
        TypeElement enumType = (TypeElement) ((DeclaredType) type).asElement();
        return enumType.getEnclosedElements().stream()
                .filter(value -> value.getKind() == ElementKind.ENUM_CONSTANT)
                .map(value -> value.getSimpleName().toString())
                .findFirst().orElseThrow();
    }

    VariableElement findField(List<VariableElement> fields, String name) {
        return fields.stream().filter(field -> field.getSimpleName().contentEquals(name)).findFirst().orElseThrow();
    }

    String helperSuffix(TypeElement type) {
        return type.getQualifiedName().toString().replaceAll("[^A-Za-z0-9_]", "_");
    }

    TypeElement requireTypeElement(String qualifiedName) {
        TypeElement type = processingEnv.getElementUtils().getTypeElement(qualifiedName);
        if (type == null) {
            throw new IllegalStateException("Validated model type is unavailable: " + qualifiedName);
        }
        return type;
    }

    private String packageName(TypeElement type) {
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(type);
        return packageElement.isUnnamed() ? "" : packageElement.getQualifiedName().toString();
    }

    String spaces(int indent) {
        return "  ".repeat(indent);
    }

    String quote(String value) {
        StringBuilder result = new StringBuilder("\"");
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '\\' -> result.append("\\\\");
                case '"' -> result.append("\\\"");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                case '\b' -> result.append("\\b");
                case '\f' -> result.append("\\f");
                default -> {
                    if (character < 0x20) {
                        result.append('\\').append(String.format("%03o", (int) character));
                    } else {
                        result.append(character);
                    }
                }
            }
        }
        return result.append('"').toString();
    }

    String charLiteral(char character) {
        return switch (character) {
            case '\\' -> "'\\\\'";
            case '\'' -> "'\\''";
            case '\n' -> "'\\n'";
            case '\r' -> "'\\r'";
            case '\t' -> "'\\t'";
            case '\b' -> "'\\b'";
            case '\f' -> "'\\f'";
            default -> character < 0x20
                    ? "'" + "\\" + String.format("%03o", (int) character) + "'"
                    : "'" + character + "'";
        };
    }

    void error(Element element, String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
