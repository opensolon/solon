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
package org.noear.solon.i18n.integration;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.aspect.MethodInterceptor;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.i18n.annotation.I18n;

/**
 * 国际化方法拦截器
 *
 * @author noear
 * @since 1.5
 */
public final class I18nInterceptor implements MethodInterceptor {
    public static final I18nInterceptor instance = new I18nInterceptor();

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Object rst = inv.invoke();

        if (rst instanceof ModelAndView) {
            Context ctx = Context.current();
            I18n anno = inv.getMethodAnnotation(I18n.class);

            if (anno == null) {
                anno = inv.getTargetAnnotation(I18n.class);
            }

            if (anno != null && ctx != null) {
                ModelAndView mv = (ModelAndView) rst;

                String bundleName = Utils.annoAlias(anno.value(), anno.bundle());
                I18nBundle bundle;

                if (Utils.isEmpty(bundleName)) {
                    bundle = I18nUtil.getMessageBundle(ctx);
                } else {
                    bundle = I18nUtil.getBundle(bundleName, ctx);
                }

                mv.put("i18n", bundle);
            }
        }

        return rst;
    }
}
