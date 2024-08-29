/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.test.data;

import org.noear.solon.test.annotation.Rollback;
import org.noear.solon.test.annotation.TestRollback;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @see 2.5
 */
public class RollbackAnno implements Rollback {
    TestRollback anno;
    public RollbackAnno(TestRollback anno){
        this.anno = anno;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return anno.annotationType();
    }

    @Override
    public boolean value() {
        return anno.value();
    }
}
