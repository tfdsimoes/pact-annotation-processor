package com.pact.processor;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactMapMatcher;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import com.pact.processor.annotation.PactRootType;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class PactDslSourceGenerator {
    private final PactDslProcessor processor;

    PactDslSourceGenerator(PactDslProcessor processor) {
        this.processor = processor;
    }
    String createSource(
            String packageName,
            TypeElement root,
            List<VariableElement> rootFields,
            List<VariableElement> bodyFields,
            String builderName,
            PactRootType rootType,
            String rootField,
            Map<String, TypeElement> modelTypes) {
        StringBuilder source = new StringBuilder();
        if (!packageName.isEmpty()) {
            source.append("package ").append(packageName).append(";\n\n");
        }
        source.append("import au.com.dius.pact.consumer.dsl.DslPart;\n")
                .append("import au.com.dius.pact.consumer.dsl.LambdaDsl;\n")
                .append("import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;\n")
                .append("import java.util.*;\n\n")
                .append("public final class ").append(builderName).append(" {\n")
                .append("  public DslPart build() {\n");

        if (rootType == PactRootType.OBJECT) {
            source.append("    return LambdaDsl.newJsonBody(body -> {\n");
            appendObjectDsl(source, root, "body", 3, new LinkedHashSet<>());
            source.append("    }).build();\n");
        } else if (rootType == PactRootType.ARRAY) {
            VariableElement field = processor.findField(bodyFields, rootField);
            PactArrayMode mode = processor.effectiveArrayMode(field);
            String factory = switch (mode) {
                case MIN_LIKE -> "newJsonArrayMinLike(" + processor.effectiveBound(field, true) + ", ";
                case MAX_LIKE -> "newJsonArrayMaxLike(" + processor.effectiveBound(field, false) + ", ";
                case MIN_MAX_LIKE -> "newJsonArrayMinMaxLike(" + processor.effectiveBound(field, true) + ", "
                        + processor.effectiveBound(field, false) + ", ";
                case UNORDERED -> "newJsonArrayUnordered(";
                case UNORDERED_MIN -> "newJsonArrayMinUnordered(" + processor.effectiveBound(field, true) + ", ";
                case UNORDERED_MAX -> "newJsonArrayMaxUnordered(" + processor.effectiveBound(field, false) + ", ";
                case UNORDERED_MIN_MAX -> "newJsonArrayMinMaxUnordered(" + processor.effectiveBound(field, true) + ", "
                        + processor.effectiveBound(field, false) + ", ";
                case EACH_LIKE -> "newJsonArray(";
                case ARRAY_CONTAINS -> "newJsonArray(";
            };
            source.append("    return LambdaDsl.").append(factory).append("array -> {\n");
            appendRootArrayDsl(source, field, "array", 3);
            source.append("    }).build();\n");
        } else {
            VariableElement field = processor.findField(bodyFields, rootField);
            source.append("    return ").append(rootValueExpression(field.asType(), field, 0)).append(";\n");
        }
        source.append("  }\n\n")
                .append("  public ").append(root.getQualifiedName()).append(" buildExpectedInstance() {\n")
                .append("    return createExpected_").append(processor.helperSuffix(root)).append("();\n")
                .append("  }\n\n");

        for (TypeElement model : modelTypes.values()) {
            appendExpectedFactory(source, model, packageName);
        }
        source.append("  private static PactDslJsonRootValue exactValue(Object value) {\n")
                .append("    PactDslJsonRootValue result = new PactDslJsonRootValue();\n")
                .append("    result.setValue(value);\n")
                .append("    return result;\n")
                .append("  }\n\n");
        source.append("}\n");
        return source.toString();
    }

    private void appendObjectDsl(
            StringBuilder source, TypeElement model, String variable, int indent, Set<String> visiting) {
        String qualifiedName = model.getQualifiedName().toString();
        if (!visiting.add(qualifiedName)) {
            return;
        }
        for (VariableElement field : processor.fieldsOf(model)) {
            if (processor.isExcluded(field)) {
                continue;
            }
            appendFieldDsl(source, field, variable, indent, visiting);
        }
        visiting.remove(qualifiedName);
    }

    private void appendFieldDsl(
            StringBuilder source, VariableElement field, String variable, int indent, Set<String> visiting) {
        TypeMirror type = field.asType();
        String name = processor.quote(field.getSimpleName().toString());
        TypeMirror valueType = type;
        if (type.getKind() == TypeKind.ARRAY) {
            valueType = ((ArrayType) type).getComponentType();
            appendArrayDsl(source, field, valueType, variable, name, indent, visiting);
            return;
        }
        if (processor.isCollection(type)) {
            valueType = processor.typeArgument(type, 0);
            appendArrayDsl(source, field, valueType, variable, name, indent, visiting);
            return;
        }
        if (processor.isMap(type)) {
            TypeMirror mapValue = processor.typeArgument(type, 1);
            PactMapMatcher mapMatcher = field.getAnnotation(PactMapMatcher.class);
            String key = processor.quote(mapMatcher == null ? "key" : mapMatcher.keyExample());
            String mapVariable = "map" + indent;
            source.append(processor.spaces(indent)).append(variable).append(".object(").append(name).append(", ")
                    .append(mapVariable).append(" -> {\n");
            if (processor.hasScalarType(mapValue) || processor.isEnum(mapValue)) {
                source.append(processor.spaces(indent + 1)).append(mapVariable).append(".eachValueLike(").append(key).append(", ")
                        .append(rootValueExpression(mapValue, field, indent)).append(");\n");
            } else {
                TypeElement nested = (TypeElement) ((DeclaredType) mapValue).asElement();
                String nestedVar = "value" + indent;
                source.append(processor.spaces(indent + 1)).append(mapVariable).append(".eachValueLike(").append(key).append(", ")
                        .append(nestedVar).append(" -> {\n");
                appendObjectDsl(source, nested, nestedVar, indent + 2, visiting);
                source.append(processor.spaces(indent + 1)).append("});\n");
            }
            source.append(processor.spaces(indent)).append("});\n");
            return;
        }
        if (processor.hasScalarType(type) || processor.isEnum(type)) {
            source.append(processor.spaces(indent)).append(scalarDslCall(variable, name, type, field)).append('\n');
            return;
        }
        TypeElement nested = (TypeElement) ((DeclaredType) type).asElement();
        String nestedVar = "nested" + indent;
        source.append(processor.spaces(indent)).append(variable).append(".object(").append(name).append(", ")
                .append(nestedVar).append(" -> {\n");
        appendObjectDsl(source, nested, nestedVar, indent + 1, visiting);
        source.append(processor.spaces(indent)).append("});\n");
    }

    private void appendArrayDsl(
            StringBuilder source,
            VariableElement field,
            TypeMirror elementType,
            String variable,
            String name,
            int indent,
            Set<String> visiting) {
        PactArrayMode mode = processor.effectiveArrayMode(field);
        int minimum = processor.effectiveBound(field, true);
        int maximum = processor.effectiveBound(field, false);
        boolean scalar = processor.hasScalarType(elementType) || processor.isEnum(elementType);
        if (maximum == 0) {
            if (scalar) {
                String value = rootValueExpression(elementType, field, indent);
                source.append(processor.spaces(indent)).append(variable).append(".maxArrayLike(")
                        .append(name).append(", 0, ").append(value).append(", 0);\n");
            } else {
                String emptyItemVariable = "emptyArrayItem" + indent;
                source.append(processor.spaces(indent)).append(variable).append(".maxArrayLike(")
                        .append(name).append(", 0, 0, ").append(emptyItemVariable).append(" -> {});\n");
            }
            return;
        }
        if (mode == PactArrayMode.ARRAY_CONTAINS) {
            PactArrayMatcher matcher = field.getAnnotation(PactArrayMatcher.class);
            source.append(processor.spaces(indent)).append(variable).append(".arrayContaining(").append(name)
                    .append(", items -> {\n");
            if (matcher.containsTypes().length > 0) {
                int index = 0;
                for (String typeName : matcher.containsTypes()) {
                    TypeElement alternative = processor.requireTypeElement(typeName);
                    String itemVariable = "contained" + indent + index++;
                    source.append(processor.spaces(indent + 1)).append("items.object(").append(itemVariable)
                            .append(" -> {\n");
                    appendObjectDsl(source, alternative, itemVariable, indent + 2, new LinkedHashSet<>());
                    source.append(processor.spaces(indent + 1)).append("});\n");
                }
            } else {
                for (String example : matcher.contains()) {
                    source.append(processor.spaces(indent + 1)).append(arrayContainsValueCall("items", elementType, example))
                            .append('\n');
                }
            }
            source.append(processor.spaces(indent)).append("});\n");
            return;
        }
        if (scalar) {
            String value = rootValueExpression(elementType, field, indent);
            int examples = exampleCount(field);
            String call = switch (mode) {
                case MIN_LIKE -> variable + ".minArrayLike(" + name + ", " + minimum + ", " + value + ", "
                        + examples + ")";
                case MAX_LIKE -> variable + ".maxArrayLike(" + name + ", " + maximum + ", " + value + ", "
                        + examples + ")";
                case MIN_MAX_LIKE -> variable + ".minMaxArrayLike(" + name + ", " + minimum + ", "
                        + maximum + ", " + value + ", " + examples + ")";
                case EACH_LIKE -> examples > 1
                        ? variable + ".eachLike(" + name + ", " + value + ", " + examples + ")"
                        : variable + ".eachLike(" + name + ", " + value + ")";
                default -> processor.unorderedArrayDsl(variable, name, mode, minimum, maximum, value);
            };
            source.append(processor.spaces(indent)).append(call).append(";\n");
            return;
        }

        String nestedVar = "item" + indent;
        if (processor.isNestedArray(elementType)) {
            String method = switch (mode) {
                case MIN_LIKE -> "eachArrayWithMinLike(" + name + ", " + minimum + ", ";
                case MAX_LIKE -> "eachArrayWithMaxLike(" + name + ", " + maximum + ", ";
                case MIN_MAX_LIKE -> "eachArrayWithMinMaxLike(" + name + ", " + minimum + ", "
                        + maximum + ", ";
                case UNORDERED -> "unorderedArray(" + name + ", ";
                case UNORDERED_MIN -> "unorderedMinArray(" + name + ", " + minimum + ", ";
                case UNORDERED_MAX -> "unorderedMaxArray(" + name + ", " + maximum + ", ";
                case UNORDERED_MIN_MAX -> "unorderedMinMaxArray(" + name + ", " + minimum + ", "
                        + maximum + ", ";
                case EACH_LIKE -> "eachArrayLike(" + name + ", ";
                case ARRAY_CONTAINS -> "eachArrayLike(" + name + ", ";
            };
            source.append(processor.spaces(indent)).append(variable).append('.').append(method)
                    .append(nestedVar).append(" -> {\n");
            appendArrayItemContentsDsl(source, elementType, nestedVar, indent + 1, visiting, field);
            source.append(processor.spaces(indent)).append("});\n");
            return;
        }
        TypeElement nested = (TypeElement) ((DeclaredType) elementType).asElement();
        String method = switch (mode) {
            case MIN_LIKE -> "minArrayLike(" + name + ", " + minimum + ", ";
            case MAX_LIKE -> "maxArrayLike(" + name + ", " + maximum + ", ";
            case MIN_MAX_LIKE -> "minMaxArrayLike(" + name + ", " + minimum + ", " + maximum + ", ";
            case UNORDERED -> "unorderedArray(" + name + ", ";
            case UNORDERED_MIN -> "unorderedMinArray(" + name + ", " + minimum + ", ";
            case UNORDERED_MAX -> "unorderedMaxArray(" + name + ", " + maximum + ", ";
            case UNORDERED_MIN_MAX -> "unorderedMinMaxArray(" + name + ", " + minimum + ", " + maximum + ", ";
            case EACH_LIKE -> "eachLike(" + name + ", ";
            case ARRAY_CONTAINS -> "eachLike(" + name + ", ";
        };
        source.append(processor.spaces(indent)).append(variable).append('.').append(method).append(nestedVar)
                .append(" -> {\n");
        appendObjectDsl(source, nested, nestedVar, indent + 1, visiting);
        source.append(processor.spaces(indent)).append("});\n");
    }

    private void appendArrayElementDsl(
            StringBuilder source,
            TypeMirror type,
            String variable,
            int indent,
            Set<String> visiting,
            VariableElement field) {
        if (processor.isArrayLike(type)) {
            TypeMirror nested = processor.arrayElementType(type);
            String method = type.getKind() == TypeKind.ARRAY ? "array" : "eachArrayLike";
            source.append(processor.spaces(indent)).append(variable).append('.').append(method).append("(inner -> {\n");
            appendArrayElementDsl(source, nested, "inner", indent + 1, visiting, field);
            source.append(processor.spaces(indent)).append("});\n");
            return;
        }
        if (processor.isScalarArrayElement(type)) {
            source.append(processor.spaces(indent)).append(rootArrayScalarCall(variable, type, field)).append('\n');
            return;
        }
        TypeElement nested = (TypeElement) ((DeclaredType) type).asElement();
        appendObjectDsl(source, nested, variable, indent, visiting);
    }

    private void appendArrayItemContentsDsl(
            StringBuilder source,
            TypeMirror arrayType,
            String variable,
            int indent,
            Set<String> visiting,
            VariableElement field) {
        TypeMirror elementType = processor.arrayElementType(arrayType);
        if (processor.hasScalarType(elementType) || processor.isEnum(elementType)) {
            source.append(processor.spaces(indent))
                    .append(rootArrayScalarCall(variable, elementType, field)).append('\n');
            return;
        }
        if (processor.isArrayLike(elementType)) {
            String innerVariable = "inner" + indent;
            String method = elementType.getKind() == TypeKind.ARRAY ? "array" : "eachArrayLike";
            source.append(processor.spaces(indent)).append(variable).append('.').append(method)
                    .append('(').append(innerVariable).append(" -> {\n");
            appendArrayItemContentsDsl(source, elementType, innerVariable, indent + 1, visiting, field);
            source.append(processor.spaces(indent)).append("});\n");
            return;
        }
        TypeElement nested = (TypeElement) ((DeclaredType) elementType).asElement();
        String itemVariable = "nestedItem" + indent;
        source.append(processor.spaces(indent)).append(variable).append(".object(").append(itemVariable)
                .append(" -> {\n");
        appendObjectDsl(source, nested, itemVariable, indent + 1, visiting);
        source.append(processor.spaces(indent)).append("});\n");
    }

    private void appendRootArrayDsl(StringBuilder source, VariableElement field, String variable, int indent) {
        if (processor.effectiveBound(field, false) == 0) {
            return;
        }
        TypeMirror type = field.asType();
        TypeMirror elementType = type.getKind() == TypeKind.ARRAY
                ? ((ArrayType) type).getComponentType()
                : processor.typeArgument(type, 0);
        if (elementType == null) {
            return;
        }
        if (processor.hasScalarType(elementType) || processor.isEnum(elementType)) {
            source.append(processor.spaces(indent)).append(rootArrayScalarCall(variable, elementType, field)).append('\n');
        } else if (processor.isArrayLike(elementType)) {
            appendArrayElementDsl(source, elementType, variable, indent, new LinkedHashSet<>(), field);
        } else {
            TypeElement nested = (TypeElement) ((DeclaredType) elementType).asElement();
            source.append(processor.spaces(indent)).append(variable).append(".object(item -> {\n");
            appendObjectDsl(source, nested, "item", indent + 1, new LinkedHashSet<>());
            source.append(processor.spaces(indent)).append("});\n");
        }
    }

    private String rootArrayScalarCall(String variable, TypeMirror type, VariableElement field) {
        PactMatcher matcher = field.getAnnotation(PactMatcher.class);
        PactMatcherKind kind = matcher == null ? PactMatcherKind.AUTO : matcher.value();
        String example = processor.explicitExample(field);
        if (matcher != null && !matcher.example().isEmpty()) {
            example = matcher.example();
        }
        if (kind == PactMatcherKind.AUTO) {
            if (processor.isBooleanType(type)) {
                kind = PactMatcherKind.BOOLEAN_TYPE;
            } else if (processor.isDecimalType(type)) {
                kind = PactMatcherKind.DECIMAL_TYPE;
            } else if (processor.isStringType(type)) {
                kind = PactMatcherKind.STRING_TYPE;
            } else if (processor.isNumericType(type)) {
                String numericSample = example == null ? processor.scalarDefaultValue(type, field) : example;
                kind = processor.fitsIntegerTypeExample(numericSample)
                        ? PactMatcherKind.INTEGER_TYPE
                        : PactMatcherKind.NUMBER_TYPE;
            } else if (processor.isUuid(type)) {
                kind = PactMatcherKind.UUID;
            } else if (processor.isDateType(type)) {
                kind = processor.temporalMatcherKind(type, field);
            } else {
                kind = PactMatcherKind.EQUALS;
            }
        }
        String value = example == null ? processor.scalarDefaultValue(type, field) : example;
        return switch (kind) {
            case NULL_VALUE -> variable + ".nullValue();";
            case EQUALS -> {
                if (processor.isBooleanType(type)) {
                    yield variable + ".booleanValue(" + Boolean.parseBoolean(value) + ");";
                }
                if (processor.isNumericType(type)) {
                    yield variable + ".numberValue(" + processor.numericExample(type, value) + ");";
                }
                yield variable + ".stringValue(" + processor.quote(value) + ");";
            }
            case STRING_TYPE -> variable + ".stringType(" + processor.quote(value) + ");";
            case NUMBER_TYPE -> variable + ".numberType(" + processor.numericExample(type, value) + ");";
            case INTEGER_TYPE -> variable + ".integerType(" + processor.longExample(value) + "L);";
            case DECIMAL_TYPE -> variable + ".decimalType(" + processor.doubleExample(type, value) + ");";
            case BOOLEAN_TYPE -> variable + ".booleanType(" + Boolean.parseBoolean(value) + ");";
            case REGEX -> variable + ".stringMatcher(" + processor.quote(matcher.regex()) + ", " + processor.quote(value) + ");";
            case DATE -> variable + ".date(" + processor.quote(processor.dateFormat(field)) + ", "
                    + processor.temporalDateExpression(type, value, field) + ");";
            case TIME -> variable + ".time(" + processor.quote(processor.dateTimeFormat(field, PactMatcherKind.TIME)) + ", "
                    + processor.temporalDateExpression(type, value, field) + ");";
            case DATETIME -> variable + ".datetime(" + processor.quote(processor.dateTimeFormat(field, PactMatcherKind.DATETIME)) + ", "
                    + processor.temporalDateExpression(type, value, field) + ");";
            case IP_V4 -> variable + ".ipV4Address();";
            case ID -> variable + ".id(" + processor.longExample(value) + "L);";
            case HEX -> variable + ".hexValue(" + processor.quote(value) + ");";
            case UUID -> variable + ".uuid(" + processor.quote(value) + ");";
            default -> variable + ".stringType(" + processor.quote(value) + ");";
        };
    }

    private String arrayContainsValueCall(String variable, TypeMirror type, String example) {
        if (processor.isBooleanType(type)) {
            return variable + ".booleanValue(" + Boolean.parseBoolean(example) + ");";
        }
        if (processor.isNumericType(type)) {
            return variable + ".numberValue(" + processor.numericExample(type, example) + ");";
        }
        return variable + ".stringValue(" + processor.quote(example) + ");";
    }

    private void appendExpectedFactory(StringBuilder source, TypeElement model, String generatedPackage) {
        String modelType = model.getQualifiedName().toString();
        String helper = "createExpected_" + processor.helperSuffix(model);
        source.append("  private static ").append(modelType).append(' ').append(helper).append("() {\n")
                .append("    ").append(modelType).append(" result = new ").append(modelType).append("();\n");
        for (VariableElement field : processor.fieldsOf(model)) {
            if (processor.isExcluded(field)) {
                continue;
            }
            String expression = expectedExpression(field.asType(), field, 0);
            ExecutableElement setter = processor.findSetter(model, field, generatedPackage);
            if (setter != null) {
                source.append("    result.").append(setter.getSimpleName()).append('(').append(expression)
                        .append(");\n");
            } else {
                source.append("    result.").append(field.getSimpleName()).append(" = ").append(expression)
                        .append(";\n");
            }
        }
        source.append("    return result;\n  }\n\n");
    }

    private String expectedExpression(TypeMirror type, VariableElement field, int depth) {
        PactMatcher matcher = field.getAnnotation(PactMatcher.class);
        if ((processor.hasScalarType(type) || processor.isEnum(type))
                && matcher != null && matcher.value() == PactMatcherKind.NULL_VALUE) {
            return "null";
        }
        if (type.getKind() == TypeKind.ARRAY) {
            TypeMirror component = ((ArrayType) type).getComponentType();
            return "new " + component + "[]{" + repeatedExpectedValues(component, field, depth) + "}";
        }
        if (processor.isCollection(type)) {
            TypeMirror component = processor.typeArgument(type, 0);
            String sample = repeatedExpectedValues(component, field, depth);
            if (processor.typeName(type).equals("java.util.Set")) {
                return "new java.util.LinkedHashSet<>(java.util.Arrays.asList(" + sample + "))";
            }
            return "java.util.Arrays.asList(" + sample + ")";
        }
        if (processor.isMap(type)) {
            TypeMirror value = processor.typeArgument(type, 1);
            PactMapMatcher mapMatcher = field.getAnnotation(PactMapMatcher.class);
            String key = mapMatcher == null ? "key" : mapMatcher.keyExample();
            return "java.util.Collections.singletonMap(" + processor.quote(key) + ", "
                    + expectedExpression(value, field, depth + 1) + ")";
        }
        if (processor.isEnum(type)) {
            String example = processor.explicitExample(field);
            if (example == null) {
                TypeElement enumType = (TypeElement) ((DeclaredType) type).asElement();
                example = enumType.getEnclosedElements().stream()
                        .filter(value -> value.getKind() == ElementKind.ENUM_CONSTANT)
                        .map(value -> value.getSimpleName().toString())
                        .findFirst().orElseThrow();
            }
            return type + "." + example;
        }
        if (processor.isNestedModel(type)) {
            TypeElement nested = (TypeElement) ((DeclaredType) type).asElement();
            return "createExpected_" + processor.helperSuffix(nested) + "()";
        }
        return scalarExampleExpression(type, field);
    }

    private String repeatedExpectedValues(TypeMirror type, VariableElement field, int depth) {
        PactArrayMatcher matcher = field.getAnnotation(PactArrayMatcher.class);
        if (matcher != null && matcher.value() == PactArrayMode.ARRAY_CONTAINS) {
            if (matcher.containsTypes().length > 0) {
                return java.util.Arrays.stream(matcher.containsTypes())
                        .map(processor::requireTypeElement)
                        .map(alternative -> "createExpected_" + processor.helperSuffix(alternative) + "()")
                        .collect(java.util.stream.Collectors.joining(", "));
            }
            return java.util.Arrays.stream(matcher.contains())
                    .map(example -> arrayContainsExpectedExpression(type, example))
                    .collect(java.util.stream.Collectors.joining(", "));
        }
        int count = depth == 0 ? exampleCount(field) : 1;
        List<String> values = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            values.add(expectedExpression(type, field, depth + 1));
        }
        return String.join(", ", values);
    }

    private String arrayContainsExpectedExpression(TypeMirror type, String example) {
        if (processor.isBooleanType(type)) {
            return Boolean.toString(Boolean.parseBoolean(example));
        }
        if (type.getKind() == TypeKind.CHAR || processor.typeName(type).equals("java.lang.Character")) {
            return processor.charLiteral(example.charAt(0));
        }
        if (processor.isStringType(type)) {
            return processor.quote(example);
        }
        if (processor.isEnum(type)) {
            return type + "." + example;
        }
        if (processor.typeName(type).equals("java.math.BigDecimal")) {
            return "new java.math.BigDecimal(" + processor.quote(example) + ")";
        }
        if (processor.typeName(type).equals("java.math.BigInteger")) {
            return "new java.math.BigInteger(" + processor.quote(example) + ")";
        }
        String parsed = processor.parsePrimitiveValue(type, example);
        if (type.getKind() == TypeKind.BYTE || processor.typeName(type).equals("java.lang.Byte")) {
            return "(byte) " + parsed;
        }
        if (type.getKind() == TypeKind.SHORT || processor.typeName(type).equals("java.lang.Short")) {
            return "(short) " + parsed;
        }
        if (type.getKind() == TypeKind.LONG || processor.typeName(type).equals("java.lang.Long")) {
            return parsed + "L";
        }
        if (type.getKind() == TypeKind.FLOAT || processor.typeName(type).equals("java.lang.Float")) {
            return parsed + "F";
        }
        if (type.getKind() == TypeKind.DOUBLE || processor.typeName(type).equals("java.lang.Double")) {
            return parsed + "D";
        }
        return parsed;
    }

    private int exampleCount(VariableElement field) {
        PactArrayMatcher matcher = field.getAnnotation(PactArrayMatcher.class);
        int maximum = matcher == null ? -1 : matcher.maxSize();
        if (maximum == 0) {
            return 0;
        }
        int minimum = matcher == null ? -1 : matcher.minSize();
        return Math.max(1, minimum);
    }

    private String scalarDslCall(String variable, String fieldName, TypeMirror type, VariableElement field) {
        PactMatcher matcher = field.getAnnotation(PactMatcher.class);
        PactMatcherKind kind = matcher == null ? PactMatcherKind.AUTO : matcher.value();
        String example = processor.explicitExample(field);
        if (matcher != null && !matcher.example().isEmpty()) {
            example = matcher.example();
        }
        if (kind == PactMatcherKind.NULL_VALUE) {
            return variable + ".nullValue(" + fieldName + ");";
        }
        if (kind == PactMatcherKind.URL) {
            StringBuilder call = new StringBuilder(variable).append(".getPactDslObject().matchUrl(")
                    .append(fieldName).append(", ").append(processor.quote(matcher.baseUrl()));
            for (String fragment : matcher.pathFragments()) {
                call.append(", ").append(processor.quote(fragment));
            }
            return call.append(");").toString();
        }
        if (kind == PactMatcherKind.REGEX) {
            String sample = example == null ? processor.scalarDefaultValue(type, field) : example;
            return variable + ".stringMatcher(" + fieldName + ", " + processor.quote(matcher.regex()) + ", "
                    + processor.quote(sample) + ");";
        }
        if (kind == PactMatcherKind.INCLUDES) {
            return variable + ".getPactDslObject().includesStr(" + fieldName + ", "
                    + processor.quote(matcher.example()) + ");";
        }

        if (kind == PactMatcherKind.AUTO) {
            if (processor.isDateType(type)) {
                if (processor.typeName(type).equals("java.time.LocalDate") || processor.typeName(type).equals("java.util.Date")) {
                    return dateDslCall(variable, fieldName, type, example, field);
                }
                if (processor.typeName(type).equals("java.time.LocalTime")) {
                    return timeDslCall(variable, fieldName, type, example, field);
                }
                return datetimeDslCall(variable, fieldName, type, example, field);
            }
            if (processor.isBooleanType(type)) {
                kind = PactMatcherKind.BOOLEAN_TYPE;
            } else if (processor.isStringType(type)) {
                kind = PactMatcherKind.STRING_TYPE;
            } else if (processor.isNumericType(type)) {
                String numericSample = example == null ? processor.scalarDefaultValue(type, field) : example;
                kind = processor.isDecimalType(type)
                        ? PactMatcherKind.DECIMAL_TYPE
                        : processor.fitsIntegerTypeExample(numericSample)
                                ? PactMatcherKind.INTEGER_TYPE
                                : PactMatcherKind.NUMBER_TYPE;
            } else if (processor.isUuid(type)) {
                kind = PactMatcherKind.UUID;
            } else if (processor.isEnum(type)) {
                kind = processor.isEnum(type) ? PactMatcherKind.EQUALS : PactMatcherKind.STRING_TYPE;
            }
        }

        String value = example == null ? processor.scalarDefaultValue(type, field) : example;
        return switch (kind) {
            case EQUALS -> equalsDslCall(variable, fieldName, type, value);
            case STRING_TYPE -> variable + ".stringType(" + fieldName + ", " + processor.quote(value) + ");";
            case NUMBER_TYPE -> variable + ".numberType(" + fieldName + ", " + processor.numericExample(type, value) + ");";
            case INTEGER_TYPE -> variable + ".integerType(" + fieldName + ", " + processor.intExample(value) + ");";
            case DECIMAL_TYPE -> variable + ".decimalType(" + fieldName + ", "
                    + processor.doubleExample(type, value) + ");";
            case BOOLEAN_TYPE -> variable + ".booleanType(" + fieldName + ", " + Boolean.parseBoolean(value) + ");";
            case DATE -> dateDslCall(variable, fieldName, type, value, field);
            case TIME -> timeDslCall(variable, fieldName, type, value, field);
            case DATETIME -> datetimeDslCall(variable, fieldName, type, value, field);
            case IP_V4 -> variable + ".ipV4Address(" + fieldName + ");";
            case ID -> variable + ".id(" + fieldName + ", " + processor.longExample(value) + "L);";
            case HEX -> variable + ".getPactDslObject().hexValue(" + fieldName + ", " + processor.quote(value) + ");";
            case UUID -> variable + ".uuid(" + fieldName + ", java.util.UUID.fromString("
                    + processor.quote(value) + "));";
            default -> variable + ".stringType(" + fieldName + ", " + processor.quote(value) + ");";
        };
    }

    private String equalsDslCall(String variable, String name, TypeMirror type, String example) {
        if (processor.isBooleanType(type)) {
            return variable + ".booleanValue(" + name + ", " + Boolean.parseBoolean(example) + ");";
        }
        if (processor.isNumericType(type)) {
            return variable + ".numberValue(" + name + ", " + processor.numericExample(type, example) + ");";
        }
        if (processor.isEnum(type)) {
            return variable + ".stringValue(" + name + ", " + processor.quote(example) + ");";
        }
        return variable + ".stringValue(" + name + ", " + processor.quote(example) + ");";
    }

    private String dateDslCall(
            String variable, String name, TypeMirror type, String example, VariableElement field) {
        String format = processor.dateFormat(field);
        String value = example == null ? processor.scalarDefaultValue(type, field) : example;
        if (processor.typeName(type).equals("java.time.LocalDate")) {
            return variable + ".date(" + name + ", " + processor.quote(format) + ", "
                    + processor.temporalParseExpression(type, value, field) + ");";
        }
        if (processor.typeName(type).equals("java.util.Date")) {
            return variable + ".date(" + name + ", " + processor.quote(format) + ", "
                    + processor.temporalDateExpression(type, value, field)
                    + ", java.util.TimeZone.getTimeZone(\"UTC\"));";
        }
        return variable + ".date(" + name + ", " + processor.quote(format) + ", new java.util.Date(0L));";
    }

    private String timeDslCall(
            String variable, String name, TypeMirror type, String example, VariableElement field) {
        String format = processor.dateTimeFormat(field, PactMatcherKind.TIME);
        String value = example == null ? processor.scalarDefaultValue(type, field) : example;
        if (processor.typeName(type).equals("java.util.Date")) {
            return variable + ".time(" + name + ", " + processor.quote(format) + ", "
                    + processor.temporalDateExpression(type, value, field)
                    + ", java.util.TimeZone.getTimeZone(\"UTC\"));";
        }
        String dateExpression = "java.util.Date.from(" + processor.temporalParseExpression(type, value, field)
                + ".atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneOffset.UTC).toInstant())";
        return variable + ".time(" + name + ", " + processor.quote(format) + ", " + dateExpression
                + ", java.util.TimeZone.getTimeZone(\"UTC\"));";
    }

    private String datetimeDslCall(
            String variable, String name, TypeMirror type, String example, VariableElement field) {
        String format = processor.dateTimeFormat(field, PactMatcherKind.DATETIME);
        String value = example == null ? processor.scalarDefaultValue(type, field) : example;
        if (processor.typeName(type).equals("java.util.Date")) {
            return variable + ".datetime(" + name + ", " + processor.quote(format) + ", "
                    + processor.temporalDateExpression(type, value, field)
                    + ", java.util.TimeZone.getTimeZone(\"UTC\"));";
        }
        if (processor.typeName(type).equals("java.time.ZonedDateTime")) {
            String parsed = processor.temporalParseExpression(type, value, field);
            return variable + ".datetime(" + name + ", " + processor.quote(format)
                    + ", java.util.Date.from(" + parsed + ".toInstant()), java.util.TimeZone.getTimeZone("
                    + parsed + ".getZone()));";
        }
        if (processor.typeName(type).equals("java.time.Instant")) {
            return variable + ".datetime(" + name + ", " + processor.quote(format)
                    + ", java.util.Date.from(" + processor.temporalParseExpression(type, value, field) + ")"
                    + ", java.util.TimeZone.getTimeZone(\"UTC\"));";
        }
        if (processor.typeName(type).equals("java.time.LocalDateTime")) {
            String parsed = processor.temporalParseExpression(type, value, field);
            return variable + ".datetime(" + name + ", " + processor.quote(format)
                    + ", java.util.Date.from(" + parsed + ".atZone(java.time.ZoneOffset.UTC).toInstant())"
                    + ", java.util.TimeZone.getTimeZone(\"UTC\"));";
        }
        if (processor.typeName(type).equals("java.time.OffsetDateTime")) {
            String parsed = processor.temporalParseExpression(type, value, field);
            return variable + ".datetime(" + name + ", " + processor.quote(format)
                    + ", java.util.Date.from(" + parsed + ".toInstant()), java.util.TimeZone.getTimeZone(\"GMT\" + "
                    + parsed + ".getOffset().getId()));";
        }
        return variable + ".datetime(" + name + ", " + processor.quote(format)
                + ", java.time.Instant.parse(\"1970-01-01T00:00:00Z\"));";
    }

    private String rootValueExpression(TypeMirror type, VariableElement field, int indent) {
        PactMatcher matcher = field == null ? null : field.getAnnotation(PactMatcher.class);
        PactMatcherKind kind = matcher == null ? PactMatcherKind.AUTO : matcher.value();
        String example = field == null ? null : processor.explicitExample(field);
        if (matcher != null && !matcher.example().isEmpty()) {
            example = matcher.example();
        }
        String value = example == null ? processor.scalarDefaultValue(type, field) : example;
        if (kind == PactMatcherKind.NULL_VALUE) {
            return "exactValue(null)";
        }
        if (kind == PactMatcherKind.REGEX && matcher != null) {
            return "PactDslJsonRootValue.stringMatcher(" + processor.quote(matcher.regex()) + ", " + processor.quote(value) + ")";
        }
        if (kind == PactMatcherKind.URL && matcher != null) {
            StringBuilder call = new StringBuilder("new PactDslJsonRootValue().matchUrl(")
                    .append(processor.quote(matcher.baseUrl()));
            for (String fragment : matcher.pathFragments()) {
                call.append(", ").append(processor.quote(fragment));
            }
            return call.append(')').toString();
        }
        if (kind == PactMatcherKind.AUTO) {
            if (processor.isBooleanType(type)) {
                kind = PactMatcherKind.BOOLEAN_TYPE;
            } else if (processor.isDecimalType(type)) {
                kind = PactMatcherKind.DECIMAL_TYPE;
            } else if (processor.isStringType(type)) {
                kind = PactMatcherKind.STRING_TYPE;
            } else if (processor.isNumericType(type)) {
                kind = processor.fitsIntegerTypeExample(value)
                        ? PactMatcherKind.INTEGER_TYPE
                        : PactMatcherKind.NUMBER_TYPE;
            } else if (processor.isUuid(type)) {
                kind = PactMatcherKind.UUID;
            } else if (processor.isEnum(type)) {
                kind = PactMatcherKind.EQUALS;
            } else if (processor.isDateType(type)) {
                kind = processor.temporalMatcherKind(type, field);
            } else {
                kind = PactMatcherKind.STRING_TYPE;
            }
        }
        return switch (kind) {
            case EQUALS -> "exactValue(" + rootJavaValue(type, value) + ")";
            case NUMBER_TYPE -> "PactDslJsonRootValue.numberType(" + processor.numericExample(type, value) + ")";
            case INTEGER_TYPE -> "PactDslJsonRootValue.integerType(" + processor.longExample(value) + "L)";
            case DECIMAL_TYPE -> "PactDslJsonRootValue.decimalType(" + processor.doubleExample(type, value) + ")";
            case BOOLEAN_TYPE -> "PactDslJsonRootValue.booleanType(" + Boolean.parseBoolean(value) + ")";
            case UUID -> "PactDslJsonRootValue.uuid(java.util.UUID.fromString(" + processor.quote(value) + "))";
            case ID -> "PactDslJsonRootValue.id(" + processor.longExample(value) + "L)";
            case HEX -> "PactDslJsonRootValue.hexValue(" + processor.quote(value) + ")";
            case IP_V4 -> "PactDslJsonRootValue.ipAddress()";
            case DATE -> "PactDslJsonRootValue.date(" + processor.quote(processor.dateFormat(field))
                    + ", " + processor.rootTemporalDateExpression(type, value, field) + ")";
            case TIME -> "PactDslJsonRootValue.time(" + processor.quote(processor.dateTimeFormat(field, PactMatcherKind.TIME))
                    + ", " + processor.rootTemporalDateExpression(type, value, field) + ")";
            case DATETIME -> "PactDslJsonRootValue.datetime("
                    + processor.quote(processor.dateTimeFormat(field, PactMatcherKind.DATETIME))
                    + ", " + processor.rootTemporalDateExpression(type, value, field) + ")";
            default -> "PactDslJsonRootValue.stringType(" + processor.quote(value) + ")";
        };
    }

    private String rootJavaValue(TypeMirror type, String example) {
        if (processor.isBooleanType(type)) {
            return Boolean.toString(Boolean.parseBoolean(example));
        }
        if (processor.isNumericType(type) || type.getKind().isPrimitive()) {
            return processor.numericExample(type, example);
        }
        if (processor.isEnum(type)) {
            return processor.quote(example);
        }
        return processor.quote(example);
    }

    private String scalarExampleExpression(TypeMirror type, VariableElement field) {
        String example = processor.explicitExample(field);
        if (example == null) {
            example = processor.scalarDefaultValue(type, field);
        }
        if (type.getKind() == TypeKind.BOOLEAN || processor.typeName(type).equals("java.lang.Boolean")) {
            return Boolean.toString(Boolean.parseBoolean(example));
        }
        if (type.getKind() == TypeKind.CHAR || processor.typeName(type).equals("java.lang.Character")) {
            return processor.charLiteral(example.charAt(0));
        }
        if (processor.isStringType(type)) {
            return processor.quote(example);
        }
        if (processor.isEnum(type)) {
            return processor.typeName(type) + "." + example;
        }
        if (processor.typeName(type).equals("java.math.BigDecimal")) {
            return "new java.math.BigDecimal(" + processor.quote(example) + ")";
        }
        if (processor.typeName(type).equals("java.math.BigInteger")) {
            return "new java.math.BigInteger(" + processor.quote(example) + ")";
        }
        if (processor.typeName(type).equals("java.time.LocalDate")) {
            return processor.temporalParseExpression(type, example, field);
        }
        if (processor.typeName(type).equals("java.time.LocalTime")) {
            return processor.temporalParseExpression(type, example, field);
        }
        if (processor.typeName(type).equals("java.time.LocalDateTime")) {
            return processor.temporalParseExpression(type, example, field);
        }
        if (processor.typeName(type).equals("java.time.OffsetDateTime")) {
            return processor.temporalParseExpression(type, example, field);
        }
        if (processor.typeName(type).equals("java.time.ZonedDateTime")) {
            return processor.temporalParseExpression(type, example, field);
        }
        if (processor.typeName(type).equals("java.time.Instant")) {
            return processor.temporalParseExpression(type, example, field);
        }
        if (processor.typeName(type).equals("java.util.UUID")) {
            return "java.util.UUID.fromString(" + processor.quote(example) + ")";
        }
        if (processor.typeName(type).equals("java.util.Date")) {
            return processor.temporalDateExpression(type, example, field);
        }
        if (type.getKind() == TypeKind.LONG || processor.typeName(type).equals("java.lang.Long")) {
            return example + "L";
        }
        if (type.getKind() == TypeKind.FLOAT || processor.typeName(type).equals("java.lang.Float")) {
            return Float.toString(Float.parseFloat(example)) + "F";
        }
        if (type.getKind() == TypeKind.DOUBLE || processor.typeName(type).equals("java.lang.Double")) {
            return Double.toString(Double.parseDouble(example)) + "D";
        }
        return example;
    }

}
