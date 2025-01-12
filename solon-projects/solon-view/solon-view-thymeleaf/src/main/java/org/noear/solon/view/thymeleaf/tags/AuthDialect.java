/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.view.thymeleaf.tags;

import org.noear.solon.auth.tags.AuthConstants;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author noear
 * @since 1.5
 */
public class AuthDialect extends AbstractProcessorDialect {

    public AuthDialect() {
        super("AuthDialect", AuthConstants.PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    private Set<IProcessor> processorSet = new LinkedHashSet<>();

    public void addProcessor(IProcessor processor) {
        processorSet.add(processor);
    }

    @Override
    public Set<IProcessor> getProcessors(String s) {
        return processorSet;
    }
}
