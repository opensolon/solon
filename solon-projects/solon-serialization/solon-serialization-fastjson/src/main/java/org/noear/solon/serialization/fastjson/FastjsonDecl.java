package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.core.util.Assert;

/**
 *
 * @author noear
 * @since 3.5
 */
public class FastjsonDecl<C,F> {
    private final boolean forSerialize;
    private C config;
    private int featuresCode;

    public FastjsonDecl(C config) {
        this.config = config;

        if (config instanceof SerializeConfig) {
            forSerialize = true;
            featuresCode = JSON.DEFAULT_GENERATE_FEATURE;
        } else {
            forSerialize = false;
            featuresCode = JSON.DEFAULT_PARSER_FEATURE;
        }
    }

    /**
     * 获取配置
     */
    public C getConfig() {
        return config;
    }

    /**
     * 重置配置
     */
    public void setConfig(C config) {
        Assert.notNull(config, "config can not be null");
        this.config = config;
    }

    /**
     * 获取特性
     */
    public int getFeatures() {
        return featuresCode;
    }

    /**
     * 设置特性
     */
    public void setFeatures(F... features) {
        if (forSerialize) {
            //序列化
            featuresCode = JSON.DEFAULT_GENERATE_FEATURE;
            addFeatures(features);
        } else {
            featuresCode = JSON.DEFAULT_PARSER_FEATURE;
            addFeatures(features);
        }
    }

    /**
     * 添加特性
     */
    public void addFeatures(F... features) {
        if (forSerialize) {
            //序列化
            for (F f1 : features) {
                SerializerFeature feature = (SerializerFeature) f1;
                featuresCode |= feature.getMask();
            }
        } else {
            for (F f1 : features) {
                Feature feature = (Feature) f1;
                featuresCode |= feature.getMask();
            }
        }
    }

    /**
     * 移除特性
     */
    public void removeFeatures(F... features) {
        if (forSerialize) {
            //序列化
            for (F f1 : features) {
                SerializerFeature feature = (SerializerFeature) f1;
                featuresCode &= ~feature.getMask();
            }
        } else {
            for (F f1 : features) {
                Feature feature = (Feature) f1;
                featuresCode &= ~feature.getMask();
            }
        }
    }
}