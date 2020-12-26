package org.noear.solon.extend.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.noear.solon.Solon;
import org.noear.solon.Utils;

import java.util.Properties;
import java.util.TimerTask;

/**
 * 配置获取任务
 *
 * <p><code>
 * @Controller
 * public class Xxx{
 *     @Inject("${hello}")
 *     String hello;
 *
 *     @Mapping("/hello")
 *     public String hello(){
 *         return hello;
 *     }
 * }
 * </code></p>
 *
 * @author 夜の孤城
 * @since 1.2
 */
class ConsulConfigTask extends TimerTask {
    ConsulClient client;

    /**
     * 配置版本号
     */
    long configVer = 0;
    String configKey;

    public ConsulConfigTask(ConsulClient client) {
        this.client = client;

        //1.优先用config.key
        configKey = Solon.cfg().get(Constants.CONFIG_KEY);

        //2.其次用app group
        if(Utils.isEmpty(configKey)){
            configKey = Solon.cfg().appGroup();
        }

        //3.再镒用app name
        if(Utils.isEmpty(configKey)){
            configKey = Solon.cfg().appName();
        }
    }

    @Override
    public void run() {
        Response<GetValue> v = client.getKVValue(configKey);
        GetValue gv = v.getValue();

        if (gv != null) {
            if (gv.getModifyIndex() > configVer) {
                configVer = gv.getModifyIndex();

                String configValue = gv.getDecodedValue();
                Properties keyValues = Utils.buildProperties(configValue);

                if (keyValues != null) {
                    Solon.cfg().putAll(keyValues);
                }
            }
        }
    }
}
