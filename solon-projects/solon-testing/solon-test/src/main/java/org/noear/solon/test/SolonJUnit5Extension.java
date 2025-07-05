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
package org.noear.solon.test;

import org.junit.jupiter.api.extension.*;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;

/**
 * @author noear
 * @since 1.10
 */
public class SolonJUnit5Extension implements TestInstanceFactory, AfterAllCallback {
    private AppContext appContext;
    private Class<?> klass;

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factory, ExtensionContext extensionContext) throws TestInstantiationException {

        try {
            //init
            klass = factory.getTestClass();
            if (appContext == null) {
                appContext = RunnerUtils.initRunner(klass);
            }

            //create
            return RunnerUtils.initTestTarget(appContext, klass);
        } catch (Throwable e) {
            throw new TestInstantiationException("Test class instantiation failed: " + factory.getTestClass().getName(), e);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        afterAllDo();
    }

    protected void afterAllDo() {
        if (klass != null && Solon.app() != null) {
            if (klass.equals(Solon.app().source())) {
                if (Solon.app() instanceof SimpleSolonApp) {
                    ((SimpleSolonApp) Solon.app()).stop();
                }
            }
        }
    }
}