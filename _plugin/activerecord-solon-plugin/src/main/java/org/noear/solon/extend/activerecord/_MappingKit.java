package org.noear.solon.extend.activerecord;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * @author noear 2021/5/24 created
 */
public class _MappingKit {
    public static void mapping(ActiveRecordPlugin arp) {
        arp.addMapping("blog", "id", Blog.class);
    }
}
