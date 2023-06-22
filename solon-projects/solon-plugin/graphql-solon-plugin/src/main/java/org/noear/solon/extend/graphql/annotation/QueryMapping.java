/*
 * Copyright 2002-2022 the original author or authors.
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
 */
package org.noear.solon.extend.graphql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.noear.solon.annotation.Alias;

/**
 * copy from org.springframework.graphql.data.method.annotation.QueryMapping
 *
 * {@code @QueryMapping} is a <em>composed annotation</em> that acts as a shortcut for {@link
 * SchemaMapping @SchemaMapping} with {@code typeName="Query"}.
 *
 * @author Rossen Stoyanchev
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SchemaMapping(typeName = "Query")
public @interface QueryMapping {

    /**
     * Alias for {@link SchemaMapping#field()}.
     */
    @Alias("field")
    String name() default "";

    /**
     * Alias for {@link SchemaMapping#field()}.
     */
    @Alias("field")
    String value() default "";

}
