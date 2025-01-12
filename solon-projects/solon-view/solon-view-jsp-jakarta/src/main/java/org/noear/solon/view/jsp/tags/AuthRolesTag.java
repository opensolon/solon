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
package org.noear.solon.view.jsp.tags;

import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author noear
 * @since 1.4
 */
public class AuthRolesTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        String nameStr = name;
        String logicalStr = logical;

        if (Utils.isEmpty(nameStr)) {
            return super.doStartTag();
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return super.doStartTag();
        }


        if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
            return TagSupport.EVAL_BODY_INCLUDE;
        } else {
            return super.doStartTag();
        }
    }

    String name;
    String logical;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogical() {
        return logical;
    }

    public void setLogical(String logical) {
        this.logical = logical;
    }
}
