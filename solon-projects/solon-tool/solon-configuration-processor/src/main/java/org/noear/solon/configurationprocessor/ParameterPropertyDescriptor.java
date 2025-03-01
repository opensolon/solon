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
 * copy from org.springframework.boot.configurationprocessor.ParameterPropertyDescriptor
 */

package org.noear.solon.configurationprocessor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.TypeKindVisitor8;
import java.util.function.Function;

/**
 * {@link PropertyDescriptor} created from a constructor or record parameter.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
abstract class ParameterPropertyDescriptor extends PropertyDescriptor {

    private final VariableElement parameter;

    ParameterPropertyDescriptor(String name, TypeMirror type, VariableElement parameter, TypeElement declaringElement,
                                ExecutableElement getter) {
        super(name, type, declaringElement, getter);
        this.parameter = parameter;

    }

    final VariableElement getParameter() {
        return this.parameter;
    }

    @Override
    protected Object resolveDefaultValue(MetadataGenerationEnvironment environment) {
        return getParameter().asType().accept(DefaultPrimitiveTypeVisitor.INSTANCE, null);
    }

    @Override
    public boolean isProperty(MetadataGenerationEnvironment env) {
        return !isNested(env); // We must be able to bind it to build the object.
    }

    /**
     * Visitor that gets the default value for primitives.
     */
    private static final class DefaultPrimitiveTypeVisitor extends TypeKindVisitor8<Object, Void> {

        static final DefaultPrimitiveTypeVisitor INSTANCE = new DefaultPrimitiveTypeVisitor();

        @Override
        public Object visitPrimitiveAsBoolean(PrimitiveType type, Void parameter) {
            return false;
        }

        @Override
        public Object visitPrimitiveAsByte(PrimitiveType type, Void parameter) {
            return (byte) 0;
        }

        @Override
        public Object visitPrimitiveAsShort(PrimitiveType type, Void parameter) {
            return (short) 0;
        }

        @Override
        public Object visitPrimitiveAsInt(PrimitiveType type, Void parameter) {
            return 0;
        }

        @Override
        public Object visitPrimitiveAsLong(PrimitiveType type, Void parameter) {
            return 0L;
        }

        @Override
        public Object visitPrimitiveAsChar(PrimitiveType type, Void parameter) {
            return null;
        }

        @Override
        public Object visitPrimitiveAsFloat(PrimitiveType type, Void parameter) {
            return 0F;
        }

        @Override
        public Object visitPrimitiveAsDouble(PrimitiveType type, Void parameter) {
            return 0D;
        }

    }

    /**
     * Visitor that gets the default using coercion.
     */
    private static final class DefaultValueCoercionTypeVisitor extends TypeKindVisitor8<Object, String> {

        static final DefaultValueCoercionTypeVisitor INSTANCE = new DefaultValueCoercionTypeVisitor();

        private <T extends Number> T parseNumber(String value, Function<String, T> parser,
                                                 PrimitiveType primitiveType) {
            try {
                return parser.apply(value);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        String.format("Invalid %s representation '%s'", primitiveType, value));
            }
        }

        @Override
        public Object visitPrimitiveAsBoolean(PrimitiveType type, String value) {
            return Boolean.parseBoolean(value);
        }

        @Override
        public Object visitPrimitiveAsByte(PrimitiveType type, String value) {
            return parseNumber(value, Byte::parseByte, type);
        }

        @Override
        public Object visitPrimitiveAsShort(PrimitiveType type, String value) {
            return parseNumber(value, Short::parseShort, type);
        }

        @Override
        public Object visitPrimitiveAsInt(PrimitiveType type, String value) {
            return parseNumber(value, Integer::parseInt, type);
        }

        @Override
        public Object visitPrimitiveAsLong(PrimitiveType type, String value) {
            return parseNumber(value, Long::parseLong, type);
        }

        @Override
        public Object visitPrimitiveAsChar(PrimitiveType type, String value) {
            if (value.length() > 1) {
                throw new IllegalArgumentException(String.format("Invalid character representation '%s'", value));
            }
            return value;
        }

        @Override
        public Object visitPrimitiveAsFloat(PrimitiveType type, String value) {
            return parseNumber(value, Float::parseFloat, type);
        }

        @Override
        public Object visitPrimitiveAsDouble(PrimitiveType type, String value) {
            return parseNumber(value, Double::parseDouble, type);
        }

    }

}
