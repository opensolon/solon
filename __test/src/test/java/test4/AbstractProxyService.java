package test4;

import org.noear.snack.ONode;
import org.noear.solon.aspect.annotation.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author kevin
 * @Date 2022-10-02 20:52
 * @Description
 */
public abstract class AbstractProxyService {

  /**
   * 内部list
   */
  private final List<String> innerList = new ArrayList<>();;

  public AbstractProxyService() {
    //内部有个线程，不断在使用 innerList的值
    new Thread(){
      @Override
      public void run() {
        while(true){
          System.out.println(ONode.stringify(innerList));
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }.start();
  }

  /**
   * 外部初始化 innerList
   * @param str
   */
  public void addStr(String str){
    innerList.add(str);
  }


  public void print(){
    System.out.println(ONode.stringify(innerList));
  }


}
