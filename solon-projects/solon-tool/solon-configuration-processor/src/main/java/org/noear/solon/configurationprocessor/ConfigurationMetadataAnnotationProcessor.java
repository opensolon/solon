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
 * copy from org.springframework.boot.configurationprocessor.ConfigurationMetadataAnnotationProcessor
 */

package org.noear.solon.configurationprocessor;

import org.noear.solon.configurationprocessor.metadata.ConfigurationMetadata;
import org.noear.solon.configurationprocessor.metadata.InvalidConfigurationMetadataException;
import org.noear.solon.configurationprocessor.metadata.ItemMetadata;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(ConfigurationMetadataAnnotationProcessor.CONFIGURATION_PROPERTIES_ANNOTATION)
public class ConfigurationMetadataAnnotationProcessor extends AbstractProcessor {

    static final String ADDITIONAL_METADATA_LOCATIONS_OPTION = "org.noear.solon.configurationprocessor.additionalMetadataLocations";

    static final String CONFIGURATION_PROPERTIES_ANNOTATION = "org.noear.solon.annotation.BindProps";

    static final String AUTOWIRED_ANNOTATION = "org.noear.solon.annotation.Inject";

    private static final Set<String> SUPPORTED_OPTIONS = Collections.singleton(ADDITIONAL_METADATA_LOCATIONS_OPTION);

    private MetadataStore metadataStore;
    private MetadataCollector metadataCollector;
    private MetadataGenerationEnvironment metadataEnv;

    protected String configurationPropertiesAnnotation() {
        return CONFIGURATION_PROPERTIES_ANNOTATION;
    }

    protected String autowiredAnnotation() {
        return AUTOWIRED_ANNOTATION;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return SUPPORTED_OPTIONS;
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.metadataStore = new MetadataStore(env);
        this.metadataCollector = new MetadataCollector(env, this.metadataStore.readMetadata());
        this.metadataEnv = new MetadataGenerationEnvironment(env, configurationPropertiesAnnotation(), autowiredAnnotation());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.metadataCollector.processing(roundEnv);
        TypeElement annotationType = this.metadataEnv.getConfigurationPropertiesAnnotationElement();
        if (annotationType != null) { // Is @ConfigurationProperties available
            for (Element element : roundEnv.getElementsAnnotatedWith(annotationType)) {
                processElement(element);
            }
        }
        if (roundEnv.processingOver()) {
            try {
                writeMetadata();
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to write metadata", ex);
            }
        }
        return false;
    }

    private void processElement(Element element) {
        try {
            AnnotationMirror annotation = this.metadataEnv.getConfigurationPropertiesAnnotation(element);
            if (annotation != null) {
                String prefix = getPrefix(annotation);
                if (element instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) element;
                    processAnnotatedTypeElement(prefix, typeElement, new ArrayDeque<>());
                } else if (element instanceof ExecutableElement) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    processExecutableElement(prefix, executableElement, new ArrayDeque<>());
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error processing configuration meta-data on " + element, ex);
        }
    }

    private void processAnnotatedTypeElement(String prefix, TypeElement element, Deque<TypeElement> seen) {
        String type = this.metadataEnv.getTypeUtils().getQualifiedName(element);
        this.metadataCollector.add(ItemMetadata.newGroup(prefix, type, type, null));
        processTypeElement(prefix, element, null, seen);
    }

    private void processExecutableElement(String prefix, ExecutableElement element, Deque<TypeElement> seen) {
        if ((!element.getModifiers().contains(Modifier.PRIVATE))
                && (TypeKind.VOID != element.getReturnType().getKind())) {
            Element returns = this.processingEnv.getTypeUtils().asElement(element.getReturnType());
            if (returns instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) returns;
                ItemMetadata group = ItemMetadata.newGroup(prefix,
                        this.metadataEnv.getTypeUtils().getQualifiedName(returns),
                        this.metadataEnv.getTypeUtils().getQualifiedName(element.getEnclosingElement()),
                        element.toString());
                if (this.metadataCollector.hasSimilarGroup(group)) {
                    this.processingEnv.getMessager()
                            .printMessage(Diagnostic.Kind.ERROR,
                                    "Duplicate @ConfigurationProperties definition for prefix '" + prefix + "'", element);
                } else {
                    this.metadataCollector.add(group);
                    processTypeElement(prefix, typeElement, element, seen);
                }
            }
        }
    }

    private void processTypeElement(String prefix, TypeElement element, ExecutableElement source,
                                    Deque<TypeElement> seen) {
        if (!seen.contains(element)) {
            seen.push(element);
            new PropertyDescriptorResolver(this.metadataEnv).resolve(element, source).forEach((descriptor) -> {
                this.metadataCollector.add(descriptor.resolveItemMetadata(prefix, this.metadataEnv));
                if (descriptor.isNested(this.metadataEnv)) {
                    TypeElement nestedTypeElement = (TypeElement) this.metadataEnv.getTypeUtils()
                            .asElement(descriptor.getType());
                    String nestedPrefix = ConfigurationMetadata.nestedPrefix(prefix, descriptor.getName());
                    processTypeElement(nestedPrefix, nestedTypeElement, source, seen);
                }
            });
            seen.pop();
        }
    }

    private String getPrefix(AnnotationMirror annotation) {
        String prefix = this.metadataEnv.getAnnotationElementStringValue(annotation, "prefix");
        if (prefix != null) {
            return prefix;
        }
        return this.metadataEnv.getAnnotationElementStringValue(annotation, "value");
    }

    protected ConfigurationMetadata writeMetadata() throws Exception {
        ConfigurationMetadata metadata = this.metadataCollector.getMetadata();
        metadata = mergeAdditionalMetadata(metadata);
        if (!metadata.getItems().isEmpty()) {
            this.metadataStore.writeMetadata(metadata);
            return metadata;
        }
        return null;
    }

    private ConfigurationMetadata mergeAdditionalMetadata(ConfigurationMetadata metadata) {
        try {
            ConfigurationMetadata merged = new ConfigurationMetadata(metadata);
            merged.merge(this.metadataStore.readAdditionalMetadata());
            return merged;
        } catch (FileNotFoundException ex) {
            // No additional metadata
        } catch (InvalidConfigurationMetadataException ex) {
            log(ex.getKind(), ex.getMessage());
        } catch (Exception ex) {
            logWarning("Unable to merge additional metadata");
            logWarning(getStackTrace(ex));
        }
        return metadata;
    }

    private String getStackTrace(Exception ex) {
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer, true));
        return writer.toString();
    }

    private void logWarning(String msg) {
        log(Diagnostic.Kind.WARNING, msg);
    }

    public void log(Diagnostic.Kind kind, String msg) {
        this.processingEnv.getMessager().printMessage(kind, msg);
    }

}
