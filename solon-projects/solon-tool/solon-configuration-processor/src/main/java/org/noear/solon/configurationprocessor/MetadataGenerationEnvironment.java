/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * copy from org.springframework.boot.configurationprocessor.MetadataGenerationEnvironment
 */

package org.noear.solon.configurationprocessor;

import org.noear.solon.configurationprocessor.fieldvalues.FieldValuesParser;
import org.noear.solon.configurationprocessor.fieldvalues.javac.JavaCompilerFieldValuesParser;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.*;

/**
 * Provide utilities to detect and validate configuration properties.
 *
 * @author Stephane Nicoll
 * @author Scott Frederick
 * @author Moritz Halbritter
 */
class MetadataGenerationEnvironment {

    private static final String NULLABLE_ANNOTATION = "org.noear.solon.lang.Nullable";

    private static final Set<String> TYPE_EXCLUDES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("com.zaxxer.hikari.IConnectionCustomizer",
            "groovy.lang.MetaClass", "groovy.text.markup.MarkupTemplateEngine", "java.io.Writer", "java.io.PrintWriter",
            "java.lang.ClassLoader", "java.util.concurrent.ThreadFactory", "jakarta.jms.XAConnectionFactory",
            "javax.sql.DataSource", "javax.sql.XADataSource", "org.apache.tomcat.jdbc.pool.PoolConfiguration",
            "org.apache.tomcat.jdbc.pool.Validator", "org.flywaydb.core.api.callback.FlywayCallback",
            "org.flywaydb.core.api.resolver.MigrationResolver")));

    private static final Set<String> DEPRECATION_EXCLUDES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "org.apache.commons.dbcp2.BasicDataSource#getPassword",
            "org.apache.commons.dbcp2.BasicDataSource#getUsername")));

    private final TypeUtils typeUtils;

    private final Elements elements;

    private final Messager messager;

    private final FieldValuesParser fieldValuesParser;

    private final Map<TypeElement, Map<String, Object>> defaultValues = new HashMap<>();

    private final String configurationPropertiesAnnotation;

    private final String autowiredAnnotation;

    MetadataGenerationEnvironment(ProcessingEnvironment environment, String configurationPropertiesAnnotation,
                                  String autowiredAnnotation) {
        this.typeUtils = new TypeUtils(environment);
        this.elements = environment.getElementUtils();
        this.messager = environment.getMessager();
        this.fieldValuesParser = resolveFieldValuesParser(environment);
        this.configurationPropertiesAnnotation = configurationPropertiesAnnotation;
        this.autowiredAnnotation = autowiredAnnotation;
    }

    private static FieldValuesParser resolveFieldValuesParser(ProcessingEnvironment env) {
        try {
            return new JavaCompilerFieldValuesParser(env);
        } catch (Throwable ex) {
            return FieldValuesParser.NONE;
        }
    }

    TypeUtils getTypeUtils() {
        return this.typeUtils;
    }

    Messager getMessager() {
        return this.messager;
    }

    /**
     * Return the default value of the field with the specified {@code name}.
     *
     * @param type the type to consider
     * @param name the name of the field
     * @return the default value or {@code null} if the field does not exist or no default
     * value has been detected
     */
    Object getFieldDefaultValue(TypeElement type, String name) {
        return this.defaultValues.computeIfAbsent(type, this::resolveFieldValues).get(name);
    }

    boolean isExcluded(TypeMirror type) {
        if (type == null) {
            return false;
        }
        String typeName = type.toString();
        if (typeName.endsWith("[]")) {
            typeName = typeName.substring(0, typeName.length() - 2);
        }
        return TYPE_EXCLUDES.contains(typeName);
    }

    boolean hasAutowiredAnnotation(ExecutableElement element) {
        return hasAnnotation(element, this.autowiredAnnotation);
    }

    boolean hasAnnotation(Element element, String type) {
        return hasAnnotation(element, type, false);
    }

    boolean hasAnnotation(Element element, String type, boolean considerMetaAnnotations) {
        if (element != null) {
            for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
                if (type.equals(annotation.getAnnotationType().toString())) {
                    return true;
                }
            }
            if (considerMetaAnnotations) {
                Set<Element> seen = new HashSet<>();
                for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
                    if (hasMetaAnnotation(annotation.getAnnotationType().asElement(), type, seen)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private boolean hasMetaAnnotation(Element annotationElement, String type, Set<Element> seen) {
        if (seen.add(annotationElement)) {
            for (AnnotationMirror annotation : annotationElement.getAnnotationMirrors()) {
                DeclaredType annotationType = annotation.getAnnotationType();
                if (type.equals(annotationType.toString())
                        || hasMetaAnnotation(annotationType.asElement(), type, seen)) {
                    return true;
                }
            }
        }
        return false;
    }

    AnnotationMirror getAnnotation(Element element, String type) {
        if (element != null) {
            for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
                if (type.equals(annotation.getAnnotationType().toString())) {
                    return annotation;
                }
            }
        }
        return null;
    }

    /**
     * Collect the annotations that are annotated or meta-annotated with the specified
     * {@link TypeElement annotation}.
     *
     * @param element        the element to inspect
     * @param annotationType the annotation to discover
     * @return the annotations that are annotated or meta-annotated with this annotation
     */
    List<Element> getElementsAnnotatedOrMetaAnnotatedWith(Element element, TypeElement annotationType) {
        LinkedList<Element> stack = new LinkedList<>();
        stack.push(element);
        collectElementsAnnotatedOrMetaAnnotatedWith(annotationType, stack);
        stack.removeFirst();
        return Collections.unmodifiableList(stack);
    }

    private boolean collectElementsAnnotatedOrMetaAnnotatedWith(TypeElement annotationType, LinkedList<Element> stack) {
        Element element = stack.peekLast();
        for (AnnotationMirror annotation : this.elements.getAllAnnotationMirrors(element)) {
            Element annotationElement = annotation.getAnnotationType().asElement();
            if (!stack.contains(annotationElement)) {
                stack.addLast(annotationElement);
                if (annotationElement.equals(annotationType)) {
                    return true;
                }
                if (!collectElementsAnnotatedOrMetaAnnotatedWith(annotationType, stack)) {
                    stack.removeLast();
                }
            }
        }
        return false;
    }

    Map<String, Object> getAnnotationElementValues(AnnotationMirror annotation) {
        Map<String, Object> values = new LinkedHashMap<>();
        annotation.getElementValues()
                .forEach((name, value) -> values.put(name.getSimpleName().toString(), getAnnotationValue(value)));
        return values;
    }

    String getAnnotationElementStringValue(AnnotationMirror annotation, String name) {
        return annotation.getElementValues()
                .entrySet()
                .stream()
                .filter((element) -> element.getKey().getSimpleName().toString().equals(name))
                .map((element) -> asString(getAnnotationValue(element.getValue())))
                .findFirst()
                .orElse(null);
    }

    private Object getAnnotationValue(AnnotationValue annotationValue) {
        Object value = annotationValue.getValue();
        if (value instanceof List) {
            List<Object> values = new ArrayList<>();
            ((List<?>) value).forEach((v) -> values.add(((AnnotationValue) v).getValue()));
            return values;
        }
        return value;
    }

    private String asString(Object value) {
        return (value == null || value.toString().isEmpty()) ? null : (String) value;
    }

    TypeElement getConfigurationPropertiesAnnotationElement() {
        return this.elements.getTypeElement(this.configurationPropertiesAnnotation);
    }

    AnnotationMirror getConfigurationPropertiesAnnotation(Element element) {
        return getAnnotation(element, this.configurationPropertiesAnnotation);
    }

    boolean hasNullableAnnotation(Element element) {
        return getAnnotation(element, NULLABLE_ANNOTATION) != null;
    }

    private Map<String, Object> resolveFieldValues(TypeElement element) {
        Map<String, Object> values = new LinkedHashMap<>();
        resolveFieldValuesFor(values, element);
        return values;
    }

    private void resolveFieldValuesFor(Map<String, Object> values, TypeElement element) {
        try {
            this.fieldValuesParser.getFieldValues(element).forEach((name, value) -> {
                if (!values.containsKey(name)) {
                    values.put(name, value);
                }
            });
        } catch (Exception ex) {
            // continue
        }
        Element superType = this.typeUtils.asElement(element.getSuperclass());
        if (superType instanceof TypeElement && superType.asType().getKind() != TypeKind.NONE) {
            resolveFieldValuesFor(values, (TypeElement) superType);
        }
    }

}
