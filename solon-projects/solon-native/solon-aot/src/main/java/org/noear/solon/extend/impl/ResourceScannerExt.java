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
package org.noear.solon.extend.impl;

import org.noear.solon.Solon;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.core.ResourceScanner;
import org.noear.solon.core.util.LogUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Predicate;

/**
 * native 运行时，优先从元数据文件（solon-resource.json）里获取
 *
 * @author songyinyin
 * @since 2.2
 */
public class ResourceScannerExt extends ResourceScanner {
    static final Logger log = LoggerFactory.getLogger(ResourceScannerExt.class);

    @Override
    public Set<String> scan(ClassLoader classLoader, String path, boolean fileMode, Predicate<String> filter) {
        Set<String> urls = super.scan(classLoader, path, fileMode, filter);

        if (fileMode == false) {
            //3.native image
            if (NativeDetector.inNativeImage()) {
                GraalvmUtil.scanResource(path, filter, urls);
                if (Solon.cfg().isDebugMode()) {
                    log.info("Native: Resource scan: " + urls.size() + ", path: " + path);
                }

                return urls;
            }
        }

        return urls;
    }
}
