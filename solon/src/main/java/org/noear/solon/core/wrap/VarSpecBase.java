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
package org.noear.solon.core.wrap;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.ActionParam;

import java.lang.reflect.AnnotatedElement;

/**
 * 变量说明 基类
 *
 * @author noear
 * @since 2.4
 */
public abstract class VarSpecBase implements VarSpec {
    private final AnnotatedElement element;
    private final ActionParam vo = new ActionParam();

    @Override
    public boolean isRequiredBody() {
        return vo.isRequiredBody;
    }

    @Override
    public boolean isRequiredHeader() {
        return vo.isRequiredHeader;
    }

    @Override
    public boolean isRequiredCookie() {
        return vo.isRequiredCookie;
    }

    @Override
    public boolean isRequiredPath() {
        return vo.isRequiredPath;
    }

    @Override
    public boolean isRequiredInput() {
        return vo.isRequiredInput;
    }

    @Override
    public String getRequiredHint() {
        if (vo.isRequiredHeader) {
            return "Required header @" + getName();
        } else if (vo.isRequiredCookie) {
            return "Required cookie @" + getName();
        } else {
            return "Required parameter @" + getName();
        }
    }

    @Override
    public String getName() {
        return vo.name;
    }


    @Override
    public String getDefaultValue() {
        return vo.defaultValue;
    }

    public VarSpecBase(AnnotatedElement element, String name) {
        this.element = element;
        this.vo.name = name;
    }

    protected void initAction() {
        //没有时，不处理
        if (Solon.app().factoryManager().hasMvcFactory()) {
            Solon.app().factoryManager().mvcFactory().resolveActionParam(vo, element);
        }
    }

    /////////////////
}
