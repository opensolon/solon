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
 * copy from org.springframework.boot.configurationprocessor.ConstructorParameterPropertyDescriptor
 */

package org.noear.solon.configurationprocessor;

import org.noear.solon.configurationprocessor.support.NestedConfigurationProperty;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link PropertyDescriptor} for a constructor parameter.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
class ConstructorParameterPropertyDescriptor extends ParameterPropertyDescriptor {

    private final ExecutableElement setter;

    private final VariableElement field;

    ConstructorParameterPropertyDescriptor(String name, TypeMirror type, VariableElement parameter,
                                           TypeElement declaringElement, ExecutableElement getter, ExecutableElement setter, VariableElement field) {
        super(name, type, parameter, declaringElement, getter);
        this.setter = setter;
        this.field = field;
    }

    @Override
    protected List<Element> getDeprecatableElements() {
        return Arrays.asList(getGetter(), this.setter, this.field);
    }

    @Override
    protected boolean isMarkedAsNested(MetadataGenerationEnvironment environment) {
        return environment.hasAnnotation(this.field, NestedConfigurationProperty.class.getCanonicalName());
    }

    @Override
    protected String resolveDescription(MetadataGenerationEnvironment environment) {
        return environment.getTypeUtils().getJavaDoc(this.field);
    }

}
