package org.noear.nami.common;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * 固定上游
 *
 * @author noear
 * @since 1.2
 */
public class UpstreamFixed implements Supplier<String> {
    protected List<String> servers;
    protected Iterator<String> tmp;

    protected String server1;

    public UpstreamFixed(List<String> servers) {
        this.servers = servers;
        if (servers.size() == 1) {
            server1 = servers.get(0);
        } else {
            tmp = servers.iterator();
        }
    }

    @Override
    public String get() {
        if (server1 == null) {
            if (tmp.hasNext() == false) {
                tmp = servers.iterator();
            }

            return tmp.next();
        } else {
            return server1;
        }
    }
}
