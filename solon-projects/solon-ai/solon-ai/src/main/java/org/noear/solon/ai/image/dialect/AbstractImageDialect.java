package org.noear.solon.ai.image.dialect;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.image.ImageConfig;
import org.noear.solon.ai.image.ImageOptions;

import java.util.List;
import java.util.Map;

/**
 * 图像模型方言基类
 *
 * @author noear
 * @since 3.1
 */
public abstract class AbstractImageDialect implements ImageDialect {
    @Override
    public String buildRequestJson(ImageConfig config, ImageOptions options, List<String> input) {
        return new ONode().build(n -> {
            if (Utils.isNotEmpty(config.getModel())) {
                n.set("model", config.getModel());
            }

            n.getOrNew("input").build(n1 -> {
                for (String m1 : input) {
                    n1.add(m1);
                }
            });

            n.set("encoding_format", "float");

            for (Map.Entry<String, Object> kv : options.options().entrySet()) {
                n.set(kv.getKey(), kv.getValue());
            }
        }).toJson();
    }
}
