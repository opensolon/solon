package test4;

import org.junit.Test;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.proxy.annotation.ProxyComponent;

/**
 * @Author kevin
 * @Date 2022-10-02 20:55
 * @Description
 */
@ProxyComponent
public class TestProxy {

  @Inject
  ProxyService proxyService;

  @Init
  public void init(){
    proxyService.init();

  }
}
