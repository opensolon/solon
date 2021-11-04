package org.springframework.context;

import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.springframework.beans.BeansException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ApplicationContext {
    default  <T> Map<String,T> getBeansOfType(Class<T> type){
      List<BeanWrap> bws=  Aop.beanFind(bw-> bw.clz().isAssignableFrom(type));
      Map<String,T> result=new HashMap<>();
      for(BeanWrap bw:bws){
          result.put(bw.name(),bw.get());
      }
      return result;
    }
    default boolean containsBean(String name){
        return Aop.has(name);
    }
    default  <T> T getBean(Class<T> type){
        T reuslt= Aop.get(type);
        if(reuslt==null){
            throw new BeansException();
        }else{
            return reuslt;
        }
    }
    default  <T> T getBean(String name){
        T reuslt= Aop.get(name);
        if(reuslt==null){
            throw new BeansException();
        }else{
            return reuslt;
        }
    }
}
