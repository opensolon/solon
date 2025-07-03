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

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;

public class SolonJUnit4ClassRunner extends BlockJUnit4ClassRunner {
    private AppContext appContext;
    private Class<?> klass;

    public SolonJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        try {
            //init
            klass = super.getTestClass().getJavaClass();
            if (appContext == null) {
                appContext = RunnerUtils.initRunner(klass);
            }

            //create
            return RunnerUtils.initTestTarget(appContext, klass);
        } catch (Exception e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        Statement original = super.withAfterClasses(statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                original.evaluate();

                //afterAll
                afterAllDo();
            }
        };
    }

    protected void afterAllDo() {
        if (klass != null && Solon.app() != null) {
            if (klass.equals(Solon.app().source())) {
                Solon.stopBlock();
            }
        }
    }
}
