package test4;

import org.noear.solon.aspect.annotation.Service;

/**
 * @Author kevin
 * @Date 2022-10-02 20:55
 * @Description
 */
@Service
public class ProxyService extends AbstractProxyService{

  public void init(){
    addStr("测试A");
    addStr("测试B");
  }


}
