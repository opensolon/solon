package labs.test4;

import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;

/**
 * @Author kevin
 * @Date 2022-10-02 20:55
 * @Description
 */
@Component
public class TestProxy {

  @Inject
  ProxyService proxyService;

  @Init
  public void init(){
    proxyService.init();

  }
}
