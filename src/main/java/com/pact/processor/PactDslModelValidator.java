package com.pact.processor;

import com.pact.processor.annotation.PactArrayMatcher;
import com.pact.processor.annotation.PactArrayMode;
import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMapMatcher;
import com.pact.processor.annotation.PactMatcher;
import com.pact.processor.annotation.PactMatcherKind;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class PactDslModelValidator {
    private static final int MAX_GENERATED_EXAMPLES = 1000;

    private final PactDslProcessor processor;
    private final javax.annotation.processing.ProcessingEnvironment processingEnv;

    PactDslModelValidator(PactDslProcessor processor) {
        this.processor = processor;
        this.processingEnv = processor.processingEnvironment();
    }
    boolean validateExpectedHelperNames(TypeElement root, Map<String, TypeElement> modelTypes) {
        Map<String, String> helperNames = new LinkedHashMap<>();
        for (TypeElement model : modelTypes.values()) {
            String suffix = processor.helperSuffix(model);
            String qualifiedName = model.getQualifiedName().toString();
            String previous = helperNames.putIfAbsent(suffix, qualifiedName);
            if (previous != null && !previous.equals(qualifiedName)) {
                processor.error(root, "Generated expected-instance helper name collision between "
                        + previous + " and " + qualifiedName + ".");
                return false;
            }
        }
        return true;
    }

    boolean validateModel(
            TypeElement model,
            Set<String> visiting,
            Map<String, TypeElement> modelTypes,
            String generatedPackage) {
        String qualifiedName = model.getQualifiedName().toString();
        if (model.getKind() != ElementKind.CLASS || model.getModifiers().contains(Modifier.ABSTRACT)) {
            processor.error(model, "Nested Pact model types must be concrete classes.");
            return false;
        }
        if (model.getEnclosingElement() instanceof TypeElement
                && !model.getModifiers().contains(Modifier.STATIC)) {
            processor.error(model, "Nested Pact model classes must be static.");
            return false;
        }
        if (!visiting.add(qualifiedName)) {
            processor.error(model, "Recursive model reference detected at " + qualifiedName + ".");
            return false;
        }
        modelTypes.putIfAbsent(qualifiedName, model);
        boolean valid = true;
        if (!processor.isTypeAccessibleFromPackage(model, generatedPackage)) {
            processor.error(model, "Model " + qualifiedName + " is not accessible from generated package "
                    + generatedPackage + ".");
            valid = false;
        }
        if (!processor.hasNoArgConstructor(model, generatedPackage)) {
            processor.error(model, "Model " + qualifiedName + " needs an accessible no-argument constructor.");
            valid = false;
        }

        for (VariableElement field : processor.fieldsOf(model)) {
            if (processor.isExcluded(field)) {
                continue;
            }
            if (field.getModifiers().contains(Modifier.FINAL)) {
                processor.error(field, "Final fields are not supported; use a writable field or setter.");
                valid = false;
                continue;
            }
            if (!processor.hasWritablePath(model, field, generatedPackage)) {
                processor.error(field, "No accessible setter or field assignment is available for this property.");
                valid = false;
            }
            if (!validateField(field, visiting, modelTypes, generatedPackage)) {
                valid = false;
            }
        }
        visiting.remove(qualifiedName);
        return valid;
    }

    private boolean validateField(
            VariableElement field,
            Set<String> visiting,
            Map<String, TypeElement> modelTypes,
            String generatedPackage) {
        TypeMirror type = field.asType();
        PactArrayMatcher arrayMatcher = field.getAnnotation(PactArrayMatcher.class);
        boolean array = type.getKind() == TypeKind.ARRAY || processor.isCollection(type);
        boolean map = processor.isMap(type);
        Long minimum = processor.minimumValue(field);
        Long maximum = processor.maximumValue(field);
        if (minimum != null && maximum != null && minimum > maximum) {
            processor.error(field, "@PactMin cannot be greater than @PactMax.");
            return false;
        }
        if ((minimum != null || maximum != null) && !processor.isNumericType(type)) {
            processor.error(field, "@PactMin and @PactMax can only constrain numeric properties.");
            return false;
        }
        PactMatcher fieldMatcher = field.getAnnotation(PactMatcher.class);
        if ((array || map) && fieldMatcher != null && fieldMatcher.value() == PactMatcherKind.INCLUDES) {
            processor.error(field, "INCLUDES matching is not supported on array or map values.");
            return false;
        }
        if (array && field.getAnnotation(PactMapMatcher.class) != null) {
            processor.error(field, "@PactMapMatcher can only be used on Map properties.");
            return false;
        }
        if (arrayMatcher != null && !array) {
            processor.error(field, "@PactArrayMatcher can only be used on array or Collection properties.");
            return false;
        }
        if (array) {
            if (!validateArrayBounds(field, arrayMatcher, visiting, modelTypes, generatedPackage)) {
                return false;
            }
            if (arrayMatcher != null && arrayMatcher.value() == PactArrayMode.ARRAY_CONTAINS
                    && arrayMatcher.containsTypes().length > 0) {
                return true;
            }
        }
        if (!map && field.getAnnotation(PactMapMatcher.class) != null) {
            processor.error(field, "@PactMapMatcher can only be used on Map properties.");
            return false;
        }
        if (map) {
            PactMapMatcher mapMatcher = field.getAnnotation(PactMapMatcher.class);
            TypeMirror keyType = processor.typeArgument(type, 0);
            TypeMirror valueType = processor.typeArgument(type, 1);
            if (mapMatcher == null) {
                processor.error(field, "Map properties require @PactMapMatcher.");
                return false;
            }
            if (keyType == null || valueType == null || !keyType.toString().equals("java.lang.String")) {
                processor.error(field, "Maps must have a concrete String key type and a concrete value type.");
                return false;
            }
            if (mapMatcher.keyExample().isBlank()) {
                processor.error(field, "A map requires a non-blank key example.");
                return false;
            }
            if (processor.isCollection(valueType) || valueType.getKind() == TypeKind.ARRAY || processor.isMap(valueType)) {
                processor.error(field, "Map values must be scalar or object values; nested containers are not supported yet.");
                return false;
            }
            return validateValueType(field, valueType, visiting, modelTypes, generatedPackage);
        }
        TypeMirror valueType = type;
        if (type.getKind() == TypeKind.ARRAY) {
            valueType = ((ArrayType) type).getComponentType();
        } else if (processor.isCollection(type)) {
            valueType = processor.typeArgument(type, 0);
            if (valueType == null) {
                processor.error(field, "Raw collections are not supported; declare the element type.");
                return false;
            }
        }
        return validateValueType(field, valueType, visiting, modelTypes, generatedPackage);
    }

    private boolean validateValueType(
            VariableElement field,
            TypeMirror type,
            Set<String> visiting,
            Map<String, TypeElement> modelTypes,
            String generatedPackage) {
        if (processor.hasScalarType(type)) {
            return validateMatcher(field, type);
        }
        if (processor.isEnum(type)) {
            TypeElement enumType = (TypeElement) ((DeclaredType) type).asElement();
            boolean hasConstants = enumType.getEnclosedElements().stream()
                    .anyMatch(value -> value.getKind() == ElementKind.ENUM_CONSTANT);
            if (!hasConstants) {
                processor.error(field, "Enum properties must declare at least one constant.");
                return false;
            }
            return validateMatcher(field, type);
        }
        if (type.getKind() == TypeKind.ARRAY) {
            return validateValueType(
                    field, ((ArrayType) type).getComponentType(), visiting, modelTypes, generatedPackage);
        }
        if (processor.isCollection(type)) {
            TypeMirror component = processor.typeArgument(type, 0);
            if (component == null) {
                processor.error(field, "Raw nested collections are not supported; declare each element type.");
                return false;
            }
            return validateValueType(field, component, visiting, modelTypes, generatedPackage);
        }
        if (type.getKind() != TypeKind.DECLARED) {
            processor.error(field, "Unsupported Pact JSON field type: " + type + ".");
            return false;
        }
        TypeElement nested = (TypeElement) ((DeclaredType) type).asElement();
        if (nested.getQualifiedName().toString().startsWith("java.")) {
            processor.error(field, "Unsupported Pact JSON field type: " + type + ".");
            return false;
        }
        if (field.getAnnotation(PactMatcher.class) != null
                || field.getAnnotation(PactExample.class) != null) {
            processor.error(field, "Scalar matcher and example annotations cannot be applied to an object property.");
            return false;
        }
        return validateModel(nested, visiting, modelTypes, generatedPackage);
    }

    private boolean validateArrayBounds(
            VariableElement field,
            PactArrayMatcher matcher,
            Set<String> visiting,
            Map<String, TypeElement> modelTypes,
            String generatedPackage) {
        int minimum = matcher == null ? -1 : matcher.minSize();
        int maximum = matcher == null ? -1 : matcher.maxSize();
        if (minimum < -1 || maximum < -1) {
            processor.error(field, "Pact array bounds must be -1 (unspecified) or non-negative.");
            return false;
        }
        if (minimum > MAX_GENERATED_EXAMPLES) {
            processor.error(field, "Pact array minimum size cannot exceed " + MAX_GENERATED_EXAMPLES
                    + " generated examples.");
            return false;
        }
        if (minimum >= 0 && maximum >= 0 && minimum > maximum) {
            processor.error(field, "The minimum array size cannot exceed the maximum array size.");
            return false;
        }
        if (processor.isSet(field.asType()) && minimum > 1) {
            processor.error(field, "Set properties cannot use a minimum size greater than one because generated examples may repeat.");
            return false;
        }
        PactArrayMode mode = processor.effectiveArrayMode(field);
        if (mode == PactArrayMode.ARRAY_CONTAINS) {
            TypeMirror elementType = processor.arrayElementType(field.asType());
            if (matcher.contains().length == 0 && matcher.containsTypes().length == 0) {
                processor.error(field, "ARRAY_CONTAINS requires at least one contains example.");
                return false;
            }
            if (matcher.contains().length > 0 && matcher.containsTypes().length > 0) {
                processor.error(field, "Use either scalar contains examples or containsTypes, not both.");
                return false;
            }
            if (minimum >= 0 || maximum >= 0) {
                processor.error(field, "ARRAY_CONTAINS cannot be combined with array size bounds.");
                return false;
            }
            if (matcher.containsTypes().length == 0 && !isScalarArrayContainsType(elementType)) {
                processor.error(field, "ARRAY_CONTAINS currently supports String, character, boolean, numeric, or enum elements.");
                return false;
            }
            if (field.getAnnotation(PactMatcher.class) != null
                    || field.getAnnotation(PactExample.class) != null) {
                processor.error(field, "ARRAY_CONTAINS examples are declared in @PactArrayMatcher.contains; field-level examples/matchers are not supported.");
                return false;
            }
            if (matcher.containsTypes().length > 0) {
                if (elementType.getKind() != TypeKind.DECLARED) {
                    processor.error(field, "Object ARRAY_CONTAINS variants require a declared base element type.");
                    return false;
                }
                Set<String> alternativeNames = new HashSet<>();
                for (String typeName : matcher.containsTypes()) {
                    TypeElement alternative = processingEnv.getElementUtils().getTypeElement(typeName);
                    if (alternative == null || alternative.getKind() != ElementKind.CLASS
                            || alternative.getModifiers().contains(Modifier.ABSTRACT)) {
                        processor.error(field, "containsTypes entries must name accessible, concrete model classes: "
                                + typeName + ".");
                        return false;
                    }
                    if (!alternativeNames.add(alternative.getQualifiedName().toString())) {
                        processor.error(field, "containsTypes must not list the same class more than once.");
                        return false;
                    }
                    if (!processingEnv.getTypeUtils().isAssignable(alternative.asType(), elementType)) {
                        processor.error(field, "ARRAY_CONTAINS alternative " + typeName
                                + " is not assignable to the array element type " + elementType + ".");
                        return false;
                    }
                    if (!validateModel(alternative, visiting, modelTypes, generatedPackage)) {
                        return false;
                    }
                }
                return true;
            }
            for (String example : matcher.contains()) {
                if (!validateArrayContainsExample(field, elementType, example)) {
                    return false;
                }
            }
            if (processor.isSet(field.asType())) {
                Set<String> uniqueExamples = new HashSet<>();
                for (String example : matcher.contains()) {
                    if (!uniqueExamples.add(processor.normalizedArrayContainsExample(elementType, example))) {
                        processor.error(field, "ARRAY_CONTAINS examples for a Set must be unique.");
                        return false;
                    }
                }
            }
            return true;
        }
        if (matcher != null && (matcher.contains().length > 0 || matcher.containsTypes().length > 0)) {
            processor.error(field, "contains and containsTypes require the ARRAY_CONTAINS mode.");
            return false;
        }
        if (mode == PactArrayMode.MIN_LIKE && minimum < 0
                || mode == PactArrayMode.MAX_LIKE && maximum < 0
                || mode == PactArrayMode.MIN_MAX_LIKE && (minimum < 0 || maximum < 0)
                || mode == PactArrayMode.UNORDERED_MIN && minimum < 0
                || mode == PactArrayMode.UNORDERED_MAX && maximum < 0
                || mode == PactArrayMode.UNORDERED_MIN_MAX && (minimum < 0 || maximum < 0)) {
            processor.error(field, "The selected array matcher requires the corresponding minSize/maxSize.");
            return false;
        }
        return true;
    }

    private boolean validateArrayContainsExample(VariableElement field, TypeMirror type, String example) {
        try {
            if (processor.isStringType(type)) {
                if ((type.getKind() == TypeKind.CHAR || processor.typeName(type).equals("java.lang.Character"))
                        && example.length() != 1) {
                    throw new IllegalArgumentException("Expected one character.");
                }
            } else if (processor.isBooleanType(type)) {
                if (!example.equals("true") && !example.equals("false")) {
                    throw new IllegalArgumentException("Expected true or false.");
                }
            } else if (processor.isNumericType(type)) {
                processor.parsePrimitiveValue(type, example);
            } else if (processor.isEnum(type)) {
                TypeElement enumType = (TypeElement) ((DeclaredType) type).asElement();
                boolean found = enumType.getEnclosedElements().stream()
                        .anyMatch(value -> value.getKind() == ElementKind.ENUM_CONSTANT
                                && value.getSimpleName().contentEquals(example.toString()));
                if (!found) {
                    throw new IllegalArgumentException("No enum constant " + example + ".");
                }
            }
        } catch (IllegalArgumentException exception) {
            processor.error(field, "Invalid ARRAY_CONTAINS example '" + example + "' for " + type + ": "
                    + exception.getMessage());
            return false;
        }
        return true;
    }

    private boolean isScalarArrayContainsType(TypeMirror type) {
        return processor.isStringType(type) || processor.isBooleanType(type) || processor.isNumericType(type) || processor.isEnum(type);
    }

    boolean fitsIntegerTypeExample(String value) {
        try {
            BigInteger parsed = new BigInteger(value);
            return parsed.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0
                    && parsed.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private boolean validateMatcher(VariableElement field, TypeMirror type) {
        PactMatcher matcher = field.getAnnotation(PactMatcher.class);
        PactExample pactExample = field.getAnnotation(PactExample.class);
        if (pactExample != null && !pactExample.format().isEmpty() && !processor.isDateType(type)) {
            processor.error(field, "Example formats can only be used with supported date/time properties.");
            return false;
        }
        if (matcher != null && !matcher.format().isEmpty() && !processor.isDateType(type)) {
            processor.error(field, "Matcher formats can only be used with supported date/time properties.");
            return false;
        }
        if (matcher != null && !matcher.regex().isEmpty() && matcher.value() != PactMatcherKind.REGEX) {
            processor.error(field, "The regex argument can only be used with REGEX matching.");
            return false;
        }
        if (matcher != null && (!matcher.baseUrl().isEmpty() || matcher.pathFragments().length > 0)
                && matcher.value() != PactMatcherKind.URL) {
            processor.error(field, "URL arguments can only be used with URL matching.");
            return false;
        }
        if (matcher == null) {
            return validateExample(field, type);
        }
        PactMatcherKind kind = matcher.value();
        if (kind == PactMatcherKind.REGEX && matcher.regex().isBlank()) {
            processor.error(field, "Pact regex matching requires a non-blank regex.");
            return false;
        }
        if (kind == PactMatcherKind.REGEX) {
            try {
                java.util.regex.Pattern.compile(matcher.regex());
            } catch (java.util.regex.PatternSyntaxException exception) {
                processor.error(field, "Invalid Pact regex: " + exception.getDescription());
                return false;
            }
        }
        if (kind == PactMatcherKind.URL && matcher.pathFragments().length == 0) {
            processor.error(field, "URL matching requires at least one path fragment.");
            return false;
        }
        if (kind == PactMatcherKind.URL && matcher.baseUrl().isBlank()) {
            processor.error(field, "URL matching requires a non-blank baseUrl.");
            return false;
        }
        if (kind == PactMatcherKind.URL) {
            String example = processor.explicitExample(field);
            if (example != null && !example.equals(processor.urlExample(matcher))) {
                processor.error(field, "A URL matcher example must match the configured baseUrl and pathFragments.");
                return false;
            }
        }
        if (kind == PactMatcherKind.INCLUDES && matcher.example().isEmpty()) {
            processor.error(field, "Pact includes matching requires an example substring.");
            return false;
        }
        if (kind == PactMatcherKind.NULL_VALUE && type.getKind().isPrimitive()) {
            processor.error(field, "A primitive property cannot use the NULL_VALUE matcher.");
            return false;
        }
        if (kind == PactMatcherKind.NULL_VALUE) {
            if (processor.minimumValue(field) != null || processor.maximumValue(field) != null) {
                processor.error(field, "NULL_VALUE matching cannot use @PactMin or @PactMax.");
                return false;
            }
            if (pactExample != null || !matcher.example().isEmpty()) {
                processor.error(field, "NULL_VALUE matching cannot specify an example.");
                return false;
            }
            if (!matcher.format().isEmpty()) {
                processor.error(field, "NULL_VALUE matching cannot specify a format.");
                return false;
            }
            return true;
        }
        if (kind == PactMatcherKind.IP_V4 && processor.explicitExample(field) != null) {
            processor.error(field, "IP_V4 matching uses Pact's generated address example; custom examples are not supported.");
            return false;
        }
        if (kind == PactMatcherKind.DATE && !Set.of("java.time.LocalDate", "java.util.Date").contains(processor.typeName(type))) {
            processor.error(field, "DATE matching requires a LocalDate or Date property.");
            return false;
        }
        if (kind == PactMatcherKind.TIME && !Set.of("java.time.LocalTime", "java.util.Date").contains(processor.typeName(type))) {
            processor.error(field, "TIME matching requires a LocalTime or Date property.");
            return false;
        }
        if (kind == PactMatcherKind.DATETIME
                && !Set.of("java.time.LocalDateTime", "java.time.OffsetDateTime", "java.time.ZonedDateTime",
                        "java.time.Instant", "java.util.Date")
                        .contains(processor.typeName(type))) {
            processor.error(field, "DATETIME matching requires a supported date-time or Date property.");
            return false;
        }
        if (kind == PactMatcherKind.URL && !processor.typeName(type).equals("java.lang.String")) {
            processor.error(field, "URL matching can only be used on String properties.");
            return false;
        }
        if (kind == PactMatcherKind.IP_V4 && !processor.typeName(type).equals("java.lang.String")) {
            processor.error(field, "IP_V4 matching requires a String property.");
            return false;
        }
        if (kind == PactMatcherKind.HEX && processor.isUuid(type)) {
            processor.error(field, "HEX matching requires a String or character property.");
            return false;
        }
        if (kind == PactMatcherKind.UUID
                && !processor.typeName(type).equals("java.lang.String") && !processor.isUuid(type)) {
            processor.error(field, "UUID matching requires a String or UUID property.");
            return false;
        }
        if (kind == PactMatcherKind.INCLUDES && !processor.isStringType(type)) {
            processor.error(field, "INCLUDES matching can only be used on String or character properties.");
            return false;
        }
        if ((kind == PactMatcherKind.STRING_TYPE || kind == PactMatcherKind.REGEX
                    || kind == PactMatcherKind.HEX || kind == PactMatcherKind.IP_V4
                    || kind == PactMatcherKind.UUID || kind == PactMatcherKind.URL)
                && !processor.isStringType(type) && !processor.isUuid(type)) {
            processor.error(field, kind + " matching requires a String-compatible property.");
            return false;
        }
        if ((kind == PactMatcherKind.NUMBER_TYPE || kind == PactMatcherKind.DECIMAL_TYPE)
                && !processor.isNumericType(type)) {
            processor.error(field, kind + " matching requires a numeric property.");
            return false;
        }
        if ((kind == PactMatcherKind.INTEGER_TYPE || kind == PactMatcherKind.ID) && !processor.isIntegralType(type)) {
            processor.error(field, kind + " matching requires an integral numeric property.");
            return false;
        }
        if (kind == PactMatcherKind.INTEGER_TYPE) {
            String integerExample = matcher.example().isEmpty() ? processor.explicitExample(field) : matcher.example();
            if (integerExample == null) {
                integerExample = processor.scalarDefaultValue(type, field);
            }
            if (integerExample != null && !fitsIntegerTypeExample(integerExample)) {
                processor.error(field, "INTEGER_TYPE examples must fit in a 32-bit integer.");
                return false;
            }
        }
        if (kind == PactMatcherKind.BOOLEAN_TYPE && !processor.isBooleanType(type)) {
            processor.error(field, "BOOLEAN_TYPE matching requires a boolean property.");
            return false;
        }
        String example = processor.explicitExample(field);
        if (example != null && !matcher.example().isEmpty() && !example.equals(matcher.example())) {
            processor.error(field, "@PactExample and @PactMatcher.example specify different values.");
            return false;
        }
        if (kind == PactMatcherKind.ID && example != null) {
            try {
                Long.parseLong(example);
            } catch (NumberFormatException exception) {
                processor.error(field, "ID examples must be valid 64-bit integers.");
                return false;
            }
        }
        if (kind == PactMatcherKind.UUID) {
            String uuidExample = matcher.example().isEmpty() ? processor.explicitExample(field) : matcher.example();
            if (uuidExample != null) {
                try {
                    UUID.fromString(uuidExample);
                } catch (IllegalArgumentException exception) {
                    processor.error(field, "UUID matcher examples must be valid UUID strings.");
                    return false;
                }
            }
        }
        if (kind == PactMatcherKind.REGEX) {
            String sample = matcher.example().isEmpty() ? processor.explicitExample(field) : matcher.example();
            if (sample == null) {
                sample = processor.scalarDefaultValue(type, field);
            }
            if (!java.util.regex.Pattern.matches(matcher.regex(), sample)) {
                processor.error(field, "The regex matcher example '" + sample + "' does not match the configured regex.");
                return false;
            }
        }
        if (kind == PactMatcherKind.HEX) {
            String sample = matcher.example().isEmpty() ? processor.explicitExample(field) : matcher.example();
            if (sample == null) {
                sample = processor.scalarDefaultValue(type, field);
            }
            if (!sample.matches("[0-9A-Fa-f]+")) {
                processor.error(field, "HEX matcher examples must contain only hexadecimal characters.");
                return false;
            }
        }
        return validateExample(field, type);
    }

    private boolean validateExample(VariableElement field, TypeMirror type) {
        String example = processor.explicitExample(field);
        boolean generatedDefault = example == null;
        if (generatedDefault) {
            example = processor.scalarDefaultValue(type, field);
        }
        if (processor.isDateType(type)) {
            try {
                java.time.format.DateTimeFormatter.ofPattern(
                        processor.dateTimeFormat(field, processor.temporalMatcherKind(type, field)),
                        java.util.Locale.ROOT);
            } catch (IllegalArgumentException exception) {
                processor.error(field, "Invalid date/time format: " + exception.getMessage());
                return false;
            }
        }
        if (processor.isBooleanType(type) && !example.equals("true") && !example.equals("false")) {
            processor.error(field, "Invalid boolean example '" + example + "'; expected true or false.");
            return false;
        }
        try {
            if (type.getKind() == TypeKind.CHAR || processor.typeName(type).equals("java.lang.Character")) {
                if (example.length() != 1) {
                    throw new IllegalArgumentException("Expected one character.");
                }
            } else if (type.getKind().isPrimitive() || processor.isBoxedNumeric(type)
                    || processor.typeName(type).equals("java.lang.Boolean")) {
                processor.parsePrimitiveValue(type, example);
                if (type.getKind() == TypeKind.FLOAT || processor.typeName(type).equals("java.lang.Float")) {
                    if (!Float.isFinite(Float.parseFloat(example))) {
                        throw new IllegalArgumentException("Numeric examples must be finite.");
                    }
                }
                if (type.getKind() == TypeKind.DOUBLE || processor.typeName(type).equals("java.lang.Double")) {
                    if (!Double.isFinite(Double.parseDouble(example))) {
                        throw new IllegalArgumentException("Numeric examples must be finite.");
                    }
                }
            } else if (processor.typeName(type).equals("java.util.UUID")) {
                UUID.fromString(example);
            } else if (processor.typeName(type).equals("java.time.LocalDate")) {
                LocalDate.parse(example, processor.temporalFormatter(field, type));
            } else if (processor.typeName(type).equals("java.time.LocalDateTime")) {
                LocalDateTime.parse(example, processor.temporalFormatter(field, type));
            } else if (processor.typeName(type).equals("java.time.OffsetDateTime")) {
                OffsetDateTime.parse(example, processor.temporalFormatter(field, type));
            } else if (processor.typeName(type).equals("java.time.ZonedDateTime")) {
                ZonedDateTime.parse(example, processor.temporalFormatter(field, type));
            } else if (processor.typeName(type).equals("java.time.Instant")) {
                Instant.from(processor.temporalFormatter(field, type).parse(example));
            } else if (processor.typeName(type).equals("java.time.LocalTime")) {
                LocalTime.parse(example, processor.temporalFormatter(field, type));
            } else if (processor.typeName(type).equals("java.util.Date")) {
                processor.validateDateExample(example, field);
            } else if (processor.typeName(type).equals("java.math.BigInteger")) {
                new BigInteger(example);
            } else if (processor.typeName(type).equals("java.math.BigDecimal")) {
                new BigDecimal(example);
            } else if (processor.isEnum(type)) {
                TypeElement enumType = (TypeElement) ((DeclaredType) type).asElement();
                String enumExample = example;
                boolean found = enumType.getEnclosedElements().stream()
                        .anyMatch(value -> value.getKind() == ElementKind.ENUM_CONSTANT
                                && value.getSimpleName().contentEquals(enumExample));
                if (!found) {
                    throw new IllegalArgumentException("No enum constant " + example + ".");
                }
            }
        } catch (java.time.DateTimeException | IllegalArgumentException exception) {
            Long minimum = processor.minimumValue(field);
            Long maximum = processor.maximumValue(field);
            if (generatedDefault && processor.isNumericType(field.asType())
                    && (minimum != null || maximum != null) && processor.isNumericType(type)) {
                processor.error(field, "Generated example '" + example + "' does not fit the numeric property type.");
                return false;
            }
            processor.error(field, "Invalid example '" + example + "' for " + type + ": " + exception.getMessage());
            return false;
        }
        PactMatcher matcher = field.getAnnotation(PactMatcher.class);
        if (matcher != null && matcher.value() == PactMatcherKind.DECIMAL_TYPE
                && (processor.isIntegralType(type)
                || processor.typeName(type).equals("java.math.BigDecimal"))
                && !processor.exactlyRepresentableAsDouble(type, example)) {
            processor.error(field, "DECIMAL_TYPE examples must be finite and exactly representable as a Pact double example.");
            return false;
        }
        if (processor.isNumericType(field.asType()) && processor.isNumericType(type)) {
            Long minimum = processor.minimumValue(field);
            Long maximum = processor.maximumValue(field);
            BigDecimal numericExample = processor.numericValueAsBigDecimal(type, example);
            if (minimum != null && numericExample.compareTo(BigDecimal.valueOf(minimum)) < 0) {
                processor.error(field, "The numeric example is below @PactMin(" + minimum + ").");
                return false;
            }
            if (maximum != null && numericExample.compareTo(BigDecimal.valueOf(maximum)) > 0) {
                processor.error(field, "The numeric example is above @PactMax(" + maximum + ").");
                return false;
            }
        }
        return true;
    }

}
