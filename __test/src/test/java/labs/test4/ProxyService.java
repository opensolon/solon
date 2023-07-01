package labs.test4;

import org.noear.solon.annotation.ProxyComponent;

/**
 * @Author kevin
 * @Date 2022-10-02 20:55
 * @Description
 */
@ProxyComponent
public class ProxyService extends AbstractProxyService{

  public void init(){
    addStr("测试A");
    addStr("测试B");
  }


}
