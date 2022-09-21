package com.yomahub.liteflow.solon.integration;

import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.solon.LiteflowProperty;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import java.util.Properties;

/**
 * @author noear
 * @since 2.9
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {
        boolean enable = Solon.cfg().getBool("liteflow.enable", false);

        if (!enable) {
            return;
        }

        //加载默认配置
        Properties defProps = Utils.loadProperties("META-INF/liteflow-default.properties");
        defProps.forEach((k, v) -> {
            Solon.cfg().putIfAbsent(k, v);
        });

        //订阅 NodeComponent 组件
        context.subWrap(NodeComponent.class, bw -> {
            FlowBus.addCommonNode(bw.name(), bw.name(), bw.clz());
        });

        //扫描相关组件
        context.beanScan(LiteflowProperty.class);
    }
}
