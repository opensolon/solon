package webapp.dso;

import org.noear.nami.Nami;
import org.noear.nami.NamiConfig;
import org.noear.nami.NamiConfiguration;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.annotation.Component;

import java.util.Map;

/**
 * @author noear 2021/4/27 created
 */
//@Component
//public class NamiConfigurationImpl implements NamiConfiguration {
//    @Override
//    public void config(NamiClient client, Nami.Builder builder) {
//        builder.filterAdd(FilterImpl.instance);
//    }
//
//    public static class FilterImpl implements Filter {
//        public static final Filter instance = new FilterImpl();
//
//        @Override
//        public void filter(NamiConfig cfg, String action, String url, Map<String, String> headers, Map<String, Object> args) {
//            headers.put("key","val");
//        }
//    }
//}
