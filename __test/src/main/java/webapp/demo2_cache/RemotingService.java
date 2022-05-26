package webapp.demo2_cache;

import org.noear.nami.annotation.NamiClient;

/**
 * @author noear 2022/3/22 created
 */
public interface RemotingService {
    String hello(String name);
}
