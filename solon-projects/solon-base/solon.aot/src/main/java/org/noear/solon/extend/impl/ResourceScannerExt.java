package org.noear.solon.extend.impl;

import org.noear.solon.Solon;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.core.ResourceScanner;
import org.noear.solon.core.util.LogUtil;

import java.util.Set;
import java.util.function.Predicate;

/**
 * native 运行时，优先从元数据文件（solon-resource.json）里获取
 *
 * @author songyinyin
 * @since 2.2
 */
public class ResourceScannerExt extends ResourceScanner {

    @Override
    public Set<String> scan(ClassLoader classLoader, String path, Predicate<String> filter) {
        Set<String> urls = super.scan(classLoader, path, filter);

        //3.native image
        if (NativeDetector.inNativeImage()) {
            GraalvmUtil.scanResource(path, filter, urls);
            if (Solon.cfg().isDebugMode()) {
                LogUtil.global().info("GraalvmUtil scan: " + urls.size() + ", path: " + path);
            }

            return urls;
        }

        return urls;
    }
}
