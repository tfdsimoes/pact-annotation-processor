package com.pact.processor;

import com.pact.processor.annotation.PactExample;
import com.pact.processor.annotation.PactMax;
import com.pact.processor.annotation.PactMin;
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
import java.util.Set;
import java.util.UUID;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class PactDslValueResolver {
    private final PactDslProcessor processor;

    PactDslValueResolver(PactDslProcessor processor) {
        this.processor = processor;
    }
    BigDecimal numericValueAsBigDecimal(TypeMirror type, String value) {
        return new BigDecimal(parsePrimitiveValue(type, value));
    }

    String normalizedArrayContainsExample(TypeMirror type, String example) {
        if (processor.isStringType(type) || processor.isEnum(type)) {
            return example;
        }
        if (processor.isBooleanType(type)) {
            return Boolean.toString(Boolean.parseBoolean(example));
        }
        return parsePrimitiveValue(type, example);
    }

    String temporalDateExpression(TypeMirror type, String value, VariableElement field) {
        String typeName = processor.typeName(type);
        if (typeName.equals("java.util.Date")) {
            return temporalParseExpression(type, value, field);
        }
        if (typeName.equals("java.time.LocalDate")) {
            return "java.util.Date.from(" + temporalParseExpression(type, value, field)
                    + ".atStartOfDay(java.time.ZoneOffset.UTC).toInstant())";
        }
        if (typeName.equals("java.time.LocalTime")) {
            return "java.util.Date.from(" + temporalParseExpression(type, value, field)
                    + ".atDate(java.time.LocalDate.of(1970, 1, 1))"
                    + ".atZone(java.time.ZoneOffset.UTC).toInstant())";
        }
        if (typeName.equals("java.time.LocalDateTime")) {
            return "java.util.Date.from(" + temporalParseExpression(type, value, field)
                    + ".atZone(java.time.ZoneOffset.UTC).toInstant())";
        }
        if (typeName.equals("java.time.OffsetDateTime")) {
            return "java.util.Date.from(" + temporalParseExpression(type, value, field) + ".toInstant())";
        }
        if (typeName.equals("java.time.ZonedDateTime")) {
            return "java.util.Date.from(" + temporalParseExpression(type, value, field) + ".toInstant())";
        }
        if (typeName.equals("java.time.Instant")) {
            return "java.util.Date.from(" + temporalParseExpression(type, value, field) + ")";
        }
        return "new java.util.Date(0L)";
    }

    String rootTemporalDateExpression(TypeMirror type, String value, VariableElement field) {
        String typeName = processor.typeName(type);
        PactMatcherKind kind = temporalMatcherKind(type, field);
        if (typeName.equals("java.util.Date")) {
            String formatter = "java.time.format.DateTimeFormatter.ofPattern("
                    + processor.quote(dateTimeFormat(field, kind)) + ", java.util.Locale.ROOT)";
            String parsed = formatter + ".parse(" + processor.quote(value) + ")";
            return switch (kind) {
                case DATE -> "java.util.Date.from(java.time.LocalDate.from(" + parsed
                        + ").atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())";
                case TIME -> "java.util.Date.from(java.time.LocalTime.from(" + parsed
                        + ").atDate(java.time.LocalDate.of(1970, 1, 1))"
                        + ".atZone(java.time.ZoneId.systemDefault()).toInstant())";
                default -> patternHasZone(dateTimeFormat(field, kind))
                        ? "java.util.Date.from(java.time.Instant.from(" + parsed + "))"
                        : "java.util.Date.from(java.time.LocalDateTime.from(" + parsed
                                + ").atZone(java.time.ZoneId.systemDefault()).toInstant())";
            };
        }
        String parsed = temporalParseExpression(type, value, field);
        if (typeName.equals("java.time.LocalDate")) {
            return "java.util.Date.from(" + parsed
                    + ".atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())";
        }
        if (typeName.equals("java.time.LocalTime")) {
            return "java.util.Date.from(" + parsed
                    + ".atDate(java.time.LocalDate.of(1970, 1, 1))"
                    + ".atZone(java.time.ZoneId.systemDefault()).toInstant())";
        }
        if (typeName.equals("java.time.LocalDateTime")) {
            return "java.util.Date.from(" + parsed
                    + ".atZone(java.time.ZoneId.systemDefault()).toInstant())";
        }
        return temporalDateExpression(type, value, field);
    }

    String temporalParseExpression(TypeMirror type, String value, VariableElement field) {
        String typeName = processor.typeName(type);
        PactMatcherKind kind = temporalMatcherKind(type, field);
        String formatter = "java.time.format.DateTimeFormatter.ofPattern("
                + processor.quote(dateTimeFormat(field, kind)) + ", java.util.Locale.ROOT)";
        if (typeName.equals("java.util.Date")) {
            String parsed = formatter + ".parse(" + processor.quote(value) + ")";
            return switch (kind) {
                case DATE -> "java.util.Date.from(java.time.LocalDate.from(" + parsed
                        + ").atStartOfDay(java.time.ZoneOffset.UTC).toInstant())";
                case TIME -> "java.util.Date.from(java.time.LocalTime.from(" + parsed
                        + ").atDate(java.time.LocalDate.of(1970, 1, 1))"
                        + ".atZone(java.time.ZoneOffset.UTC).toInstant())";
                default -> patternHasZone(dateTimeFormat(field, kind))
                        ? "java.util.Date.from(java.time.Instant.from(" + parsed + "))"
                        : "java.util.Date.from(java.time.LocalDateTime.from(" + parsed
                                + ").atZone(java.time.ZoneOffset.UTC).toInstant())";
            };
        }
        if (typeName.equals("java.time.LocalDate")) {
            return "java.time.LocalDate.parse(" + processor.quote(value) + ", " + formatter + ")";
        }
        if (typeName.equals("java.time.LocalTime")) {
            return "java.time.LocalTime.parse(" + processor.quote(value) + ", " + formatter + ")";
        }
        if (typeName.equals("java.time.LocalDateTime")) {
            return "java.time.LocalDateTime.parse(" + processor.quote(value) + ", " + formatter + ")";
        }
        if (typeName.equals("java.time.OffsetDateTime")) {
            return "java.time.OffsetDateTime.parse(" + processor.quote(value) + ", " + formatter + ")";
        }
        if (typeName.equals("java.time.ZonedDateTime")) {
            return "java.time.ZonedDateTime.parse(" + processor.quote(value) + ", " + formatter + ")";
        }
        return "java.time.Instant.from(" + formatter + ".parse(" + processor.quote(value) + "))";
    }

    java.time.format.DateTimeFormatter temporalFormatter(VariableElement field, TypeMirror type) {
        return java.time.format.DateTimeFormatter.ofPattern(
                dateTimeFormat(field, temporalMatcherKind(type, field)), java.util.Locale.ROOT);
    }

    PactMatcherKind temporalMatcherKind(TypeMirror type, VariableElement field) {
        PactMatcher matcher = field == null ? null : field.getAnnotation(PactMatcher.class);
        if (matcher != null && Set.of(PactMatcherKind.DATE, PactMatcherKind.TIME, PactMatcherKind.DATETIME)
                .contains(matcher.value())) {
            return matcher.value();
        }
        return switch (processor.typeName(type)) {
            case "java.time.LocalDate", "java.util.Date" -> PactMatcherKind.DATE;
            case "java.time.LocalTime" -> PactMatcherKind.TIME;
            default -> PactMatcherKind.DATETIME;
        };
    }

    private boolean patternHasZone(String pattern) {
        boolean quoted = false;
        for (int index = 0; index < pattern.length(); index++) {
            char current = pattern.charAt(index);
            if (current == '\'') {
                if (index + 1 < pattern.length() && pattern.charAt(index + 1) == '\'') {
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (!quoted && "XxZOzVv".indexOf(current) >= 0) {
                return true;
            }
        }
        return false;
    }

    void validateDateExample(String example, VariableElement field) {
        PactMatcherKind kind = temporalMatcherKind(field.asType(), field);
        java.time.format.DateTimeFormatter formatter = temporalFormatter(field, field.asType());
        switch (kind) {
            case DATE -> LocalDate.parse(example, formatter);
            case TIME -> LocalTime.parse(example, formatter);
            default -> {
                java.time.temporal.TemporalAccessor parsed = formatter.parse(example);
                if (parsed.isSupported(java.time.temporal.ChronoField.INSTANT_SECONDS)) {
                    Instant.from(parsed);
                } else {
                    LocalDateTime.from(parsed);
                }
            }
        }
    }

    String scalarDefaultValue(TypeMirror type, VariableElement field) {
        PactMatcher matcher = field == null ? null : field.getAnnotation(PactMatcher.class);
        if (matcher != null) {
            if (matcher.value() == PactMatcherKind.UUID) {
                return "00000000-0000-0000-0000-000000000001";
            }
            if (matcher.value() == PactMatcherKind.HEX) {
                return type.getKind() == TypeKind.CHAR || processor.typeName(type).equals("java.lang.Character")
                        ? "A" : "AB12";
            }
            if (matcher.value() == PactMatcherKind.IP_V4) {
                return "127.0.0.1";
            }
            if (matcher.value() == PactMatcherKind.URL) {
                return urlExample(matcher);
            }
        }
        if (processor.isEnum(type)) {
            return processor.firstEnumConstant(type);
        }
        if (processor.isStringType(type)) {
            return type.getKind() == TypeKind.CHAR || processor.typeName(type).equals("java.lang.Character")
                    ? "x" : "example";
        }
        if (type.getKind() == TypeKind.BOOLEAN || processor.typeName(type).equals("java.lang.Boolean")) {
            return "true";
        }
        if (type.getKind() == TypeKind.CHAR || processor.typeName(type).equals("java.lang.Character")) {
            return "x";
        }
        if (processor.isDecimalType(type)) {
            return "1.5";
        }
        if (processor.typeName(type).equals("java.time.LocalDate")) {
            return "2024-01-02";
        }
        if (processor.typeName(type).equals("java.time.LocalTime")) {
            return "03:04:05";
        }
        if (processor.typeName(type).equals("java.time.LocalDateTime")) {
            return "2024-01-02T03:04:05";
        }
        if (processor.typeName(type).equals("java.time.OffsetDateTime")) {
            return "2024-01-02T03:04:05Z";
        }
        if (processor.typeName(type).equals("java.time.ZonedDateTime")) {
            return "2024-01-02T03:04:05Z";
        }
        if (processor.typeName(type).equals("java.time.Instant")) {
            return "2024-01-02T03:04:05Z";
        }
        if (processor.typeName(type).equals("java.util.UUID")) {
            return "00000000-0000-0000-0000-000000000001";
        }
        if (processor.typeName(type).equals("java.util.Date")) {
            PactMatcherKind kind = temporalMatcherKind(type, field);
            return switch (kind) {
                case TIME -> "00:00:00";
                case DATETIME -> "1970-01-01T00:00:00Z";
                default -> "1970-01-01";
            };
        }
        if (field != null && processor.isNumericType(field.asType())) {
            Long minimum = minimumValue(field);
            Long maximum = maximumValue(field);
            long sample = 1;
            if (minimum != null && sample < minimum) {
                sample = minimum;
            }
            if (maximum != null && sample > maximum) {
                sample = maximum;
            }
            return Long.toString(sample);
        }
        return "1";
    }

    String urlExample(PactMatcher matcher) {
        String url = matcher.baseUrl();
        for (String fragment : matcher.pathFragments()) {
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += fragment;
        }
        return url;
    }

    String explicitExample(VariableElement field) {
        PactExample pactExample = field.getAnnotation(PactExample.class);
        if (pactExample != null) {
            return pactExample.value();
        }
        PactMatcher matcher = field.getAnnotation(PactMatcher.class);
        return matcher == null || matcher.example().isEmpty() ? null : matcher.example();
    }

    String dateFormat(VariableElement field) {
        return dateTimeFormat(field, PactMatcherKind.DATE);
    }

    String dateTimeFormat(VariableElement field, PactMatcherKind kind) {
        PactExample example = field.getAnnotation(PactExample.class);
        if (example != null && !example.format().isEmpty()) {
            return example.format();
        }
        PactMatcher matcher = field.getAnnotation(PactMatcher.class);
        if (matcher != null && !matcher.format().isEmpty()) {
            return matcher.format();
        }
        if (kind == PactMatcherKind.DATETIME && field != null
                && processor.typeName(field.asType()).equals("java.time.LocalDateTime")) {
            return "yyyy-MM-dd'T'HH:mm:ss";
        }
        return switch (kind) {
            case TIME -> "HH:mm:ss";
            case DATETIME -> "yyyy-MM-dd'T'HH:mm:ssXXX";
            default -> "yyyy-MM-dd";
        };
    }

    Long minimumValue(VariableElement field) {
        PactMin annotation = field.getAnnotation(PactMin.class);
        return annotation == null ? null : annotation.value();
    }

    Long maximumValue(VariableElement field) {
        PactMax annotation = field.getAnnotation(PactMax.class);
        return annotation == null ? null : annotation.value();
    }

    String numericExample(TypeMirror type, String value) {
        if (processor.typeName(type).equals("java.math.BigDecimal")) {
            return "new java.math.BigDecimal(" + processor.quote(value) + ")";
        }
        if (processor.typeName(type).equals("java.math.BigInteger")) {
            return "new java.math.BigInteger(" + processor.quote(value) + ")";
        }
        if (type.getKind() == TypeKind.FLOAT || processor.typeName(type).equals("java.lang.Float")) {
            return Float.toString(Float.parseFloat(value));
        }
        if (type.getKind() == TypeKind.DOUBLE || processor.typeName(type).equals("java.lang.Double")) {
            return Double.toString(Double.parseDouble(value));
        }
        if (processor.isIntegralType(type) && !processor.fitsIntegerTypeExample(value)) {
            return value.replaceAll("[lL]$", "") + "L";
        }
        return value;
    }

    String intExample(String value) {
        return "(int) " + longExample(value);
    }

    String longExample(String value) {
        return "(long) " + value.replaceAll("[lL]$", "");
    }

    String doubleExample(TypeMirror type, String value) {
        if (type.getKind() == TypeKind.FLOAT || processor.typeName(type).equals("java.lang.Float")) {
            return "(double) " + Float.toString(Float.parseFloat(value));
        }
        if (processor.typeName(type).equals("java.math.BigInteger")) {
            return "new java.math.BigInteger(" + processor.quote(value) + ").doubleValue()";
        }
        if (processor.typeName(type).equals("java.math.BigDecimal")) {
            return "new java.math.BigDecimal(" + processor.quote(value) + ").doubleValue()";
        }
        return "(double) " + value.replaceAll("[dDfF]$", "");
    }

    boolean exactlyRepresentableAsDouble(TypeMirror type, String value) {
        BigDecimal original = processor.typeName(type).equals("java.math.BigInteger")
                ? new BigDecimal(new BigInteger(value))
                : new BigDecimal(value);
        double parsed = original.doubleValue();
        if (!Double.isFinite(parsed)) {
            return false;
        }
        BigDecimal doubleValue = BigDecimal.valueOf(parsed);
        if (processor.isIntegralType(type)) {
            try {
                return doubleValue.toBigIntegerExact().equals(original.toBigIntegerExact());
            } catch (ArithmeticException exception) {
                return false;
            }
        }
        return doubleValue.compareTo(original) == 0;
    }

    String parsePrimitiveValue(TypeMirror type, String value) {
        return switch (type.getKind()) {
            case BYTE -> Byte.toString(Byte.parseByte(value));
            case SHORT -> Short.toString(Short.parseShort(value));
            case INT -> Integer.toString(Integer.parseInt(value));
            case LONG -> Long.toString(Long.parseLong(value));
            case FLOAT -> Float.toString(Float.parseFloat(value));
            case DOUBLE -> Double.toString(Double.parseDouble(value));
            case BOOLEAN -> Boolean.toString(Boolean.parseBoolean(value));
            default -> {
                if (processor.typeName(type).equals("java.lang.Boolean")) {
                    yield Boolean.toString(Boolean.parseBoolean(value));
                }
                if (processor.typeName(type).equals("java.lang.Byte")) {
                    yield Byte.toString(Byte.parseByte(value));
                }
                if (processor.typeName(type).equals("java.lang.Short")) {
                    yield Short.toString(Short.parseShort(value));
                }
                if (processor.typeName(type).equals("java.lang.Integer")) {
                    yield Integer.toString(Integer.parseInt(value));
                }
                if (processor.typeName(type).equals("java.lang.Long")) {
                    yield Long.toString(Long.parseLong(value));
                }
                if (processor.typeName(type).equals("java.lang.Float")) {
                    yield Float.toString(Float.parseFloat(value));
                }
                if (processor.typeName(type).equals("java.lang.Double")) {
                    yield Double.toString(Double.parseDouble(value));
                }
                if (processor.typeName(type).equals("java.math.BigDecimal")) {
                    yield new BigDecimal(value).toString();
                }
                if (processor.typeName(type).equals("java.math.BigInteger")) {
                    yield new BigInteger(value).toString();
                }
                yield value;
            }
        };
    }

}
