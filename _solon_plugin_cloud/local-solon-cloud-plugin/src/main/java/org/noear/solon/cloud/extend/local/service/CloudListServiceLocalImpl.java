package org.noear.solon.cloud.extend.local.service;

import org.noear.snack.ONode;
import org.noear.solon.cloud.extend.local.impl.CloudLocalUtils;
import org.noear.solon.cloud.service.CloudListService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 云端名单（本地摸拟实现）
 *
 * @author noear
 * @since 1.11
 */
public class CloudListServiceLocalImpl implements CloudListService {
    static final String LIST_KEY_FORMAT = "list@%s-%s.json";
    Map<String, List<String>> listMap = new HashMap<>();

    @Override
    public boolean inList(String names, String type, String value) {
        for (String name : names.split(",")) {
            try {
                if (inListDo(name, type, value)) {
                    return true;
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        return false;
    }

    private boolean inListDo(String name, String type, String value) throws IOException {
        String listKey = String.format(LIST_KEY_FORMAT, name, type);
        List<String> listVal = listMap.get(listKey);

        if (listVal == null) {
            synchronized (listMap) {
                listVal = listMap.get(listKey);

                if (listVal == null) {
                    String value2 = CloudLocalUtils.getValue(listKey);

                    if (value2 == null) {
                        listVal = new ArrayList<>();
                    } else {
                        listVal = new ArrayList<>();

                        ONode oNode = ONode.load(value2);
                        if (oNode.isArray()) {
                            for (ONode o1 : oNode.ary()) {
                                listVal.add(o1.getString());
                            }
                        }
                    }

                    listMap.put(listKey, listVal);
                }
            }
        }

        return listVal.contains(value);
    }
}