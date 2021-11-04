package com.sqltoy;

import com.sqltoy.helloworld.FruitMapper;
import com.sqltoy.helloworld.service.FruitOrderService;

import com.sqltoy.helloworld.vo.FruitOrderVO;
import org.noear.solon.Solon;

import org.noear.solon.core.Aop;
import org.sagacity.sqltoy.model.Page;

import java.math.BigDecimal;


public class SqlToyDemoApplication {
    public static void main(String[] args) {
       Solon.start(SqlToyDemoApplication.class,args);
        FruitOrderService service=Aop.get(FruitOrderService.class);

        FruitOrderVO fruitOrderVO=new FruitOrderVO();
        fruitOrderVO.setFruitName("test");
        fruitOrderVO.setOrderMonth(1);
        fruitOrderVO.setSaleCount(new BigDecimal(2));
        fruitOrderVO.setSalePrice(new BigDecimal(1));
        fruitOrderVO.setTotalAmt(new BigDecimal(1));
        service.searchFruitOrder(new Page(),fruitOrderVO);
//        Aop.getAsyn(FruitMapper.class,bw->{
//            FruitMapper mapper = bw.raw();
//            System.out.println("xxxx:"+mapper.countOrder(fruitOrderVO));
//        });
    }
}
