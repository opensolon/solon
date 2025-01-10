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
package org.noear.solon.flow.driver;

import org.noear.liquor.eval.CodeSpec;
import org.noear.liquor.eval.Exprs;
import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.ChainDriver;
import org.noear.solon.flow.core.Task;

/**
 * 脚本流驱动器
 *
 * @author noear
 * @since 3.0
 * */
public class ScriptFlowDriver implements ChainDriver {
    @Override
    public boolean handleCondition(ChainContext context, Condition condition) throws Exception {
        return (boolean) Exprs.eval(condition.expr(), context.model());
    }

    @Override
    public void handleTask(ChainContext context, Task task) throws Exception {
        CodeSpec codeSpec = new CodeSpec(task.expr());
        Object[] args = codeSpec.bind(context.model());
        Exprs.eval(codeSpec, args);
    }
}