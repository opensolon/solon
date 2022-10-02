package test4;

import org.junit.Test;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

/**
 * @Author kevin
 * @Date 2022-10-02 20:55
 * @Description
 */
@Service
public class TestProxy {

  @Inject
  ProxyService proxyService;

  @Init
  public void init(){
    proxyService.init();

  }
}
