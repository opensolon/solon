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
package org.noear.solon.core.mvc;

import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.Constants;
import org.noear.solon.core.handle.ActionParam;

import java.lang.reflect.AnnotatedElement;

/**
 * Action 参数分析器
 *
 * @author noear
 * @since 2.7
 */
public class ActionParamResolver {

    /**
     * 分析
     */
    public static void resolve(ActionParam vo, AnnotatedElement element) {
        // 分析 Body 注解
        if (resolveBody(vo, element)) {
            return;
        }
        // 分析 Param 注解
        if (resolveParam(vo, element)) {
            return;
        }
        // 分析 Path 注解
        if (resolvePath(vo, element)) {
            return;
        }
        // 分析 Header 注解
        if (resolveHeader(vo, element)) {
            return;
        }
        // 分析 Cookie 注解
        resolveCookie(vo, element);
    }

    /**
     * 分析 body 注解
     */
    private static boolean resolveBody(ActionParam vo, AnnotatedElement element) {
        Body bodyAnno = element.getAnnotation(Body.class);

        if (bodyAnno == null) {
            return false;
        }

        vo.isRequiredBody = true;
        return true;
    }

    /**
     * 分析 param 注解
     */
    private static boolean resolveParam(ActionParam vo, AnnotatedElement element) {
        Param paramAnno = element.getAnnotation(Param.class);

        if (paramAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(paramAnno.defaultValue()) == false) {
            vo.defaultValue = paramAnno.defaultValue();
        }

        vo.isRequiredInput = paramAnno.required();

        return true;
    }

    private static boolean resolvePath(ActionParam vo, AnnotatedElement element) {
        Path pathAnno = element.getAnnotation(Path.class);

        if (pathAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(pathAnno.value(), pathAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(pathAnno.defaultValue()) == false) {
            vo.defaultValue = pathAnno.defaultValue();
        }

        vo.isRequiredInput = pathAnno.required();
        vo.isRequiredPath = true;
        return true;
    }

    /**
     * 分析 header 注解
     */
    private static boolean resolveHeader(ActionParam vo, AnnotatedElement element) {
        Header headerAnno = element.getAnnotation(Header.class);

        if (headerAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(headerAnno.value(), headerAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(headerAnno.defaultValue()) == false) {
            vo.defaultValue = headerAnno.defaultValue();
        }

        vo.isRequiredInput = headerAnno.required();
        vo.isRequiredHeader = true;

        return true;
    }

    /**
     * 分析 cookie 注解
     */
    private static boolean resolveCookie(ActionParam vo, AnnotatedElement element) {
        Cookie cookieAnno = element.getAnnotation(Cookie.class);

        if (cookieAnno == null) {
            return false;
        }

        String name2 = Utils.annoAlias(cookieAnno.value(), cookieAnno.name());
        if (Utils.isNotEmpty(name2)) {
            vo.name = name2;
        }

        if (Constants.PARM_UNDEFINED_VALUE.equals(cookieAnno.defaultValue()) == false) {
            vo.defaultValue = cookieAnno.defaultValue();
        }

        vo.isRequiredInput = cookieAnno.required();
        vo.isRequiredCookie = true;

        return true;
    }
}
