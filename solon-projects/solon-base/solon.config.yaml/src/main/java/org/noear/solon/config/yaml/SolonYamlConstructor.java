package org.noear.solon.config.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 增加类型映射解析支持（将 "!XXX: xxx" 转为普通属性）
 *
 * @author noear
 * @since 2.3
 */
public class SolonYamlConstructor extends Constructor {
    ConstructMappingAsMap constructMappingAsMap;

    public SolonYamlConstructor() {
        super(new LoaderOptions());
        constructMappingAsMap = new ConstructMappingAsMap();
    }

    @Override
    protected final Construct getConstructor(Node node) {
        if (node.getTag().getValue().startsWith("!")) {
            node.setType(Map.class);
            return constructMappingAsMap;
        } else {
            return super.getConstructor(node);
        }
    }

    protected class ConstructMappingAsMap implements Construct {
        @Override
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            Object value = constructMapping(mnode);


            Map<String, Object> data = new LinkedHashMap<>();
            data.put(node.getTag().getValue(), value);
            return data;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void construct2ndStep(Node node, Object object) {
            constructMapping2ndStep((MappingNode) node, (Map<Object, Object>) object);
        }
    }
}