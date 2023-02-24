package webapp.demo2_mvc;

import org.noear.solon.proxy.annotation.ProxyComponent;

/**
 * @author noear 2021/6/28 created
 */
@ProxyComponent
public class CmdServiceImpl implements CmdService{
    public String name(String name) {
        return name;
    }
}
