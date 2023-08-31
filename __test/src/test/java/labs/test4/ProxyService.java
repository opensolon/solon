package labs.test4;

import org.noear.solon.annotation.Component;

/**
 * @Author kevin
 * @Date 2022-10-02 20:55
 * @Description
 */
@Component
public class ProxyService extends AbstractProxyService{

  public void init(){
    addStr("测试A");
    addStr("测试B");
  }


}
