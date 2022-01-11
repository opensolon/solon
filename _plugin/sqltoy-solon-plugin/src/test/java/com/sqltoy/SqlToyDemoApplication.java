package com.sqltoy;

import com.itranswarp.compiler.JavaStringCompiler;
import com.sqltoy.helloworld.FruitMapper;
import com.sqltoy.helloworld.service.FruitOrderService;

import com.sqltoy.helloworld.vo.FruitOrderVO;
import org.noear.solon.Solon;

import org.noear.solon.core.Aop;
import org.noear.solon.extend.sqltoy.mapper.AbstractMapper;
import org.noear.solon.extend.sqltoy.mapper.ProxyClassBuilder;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;


public class SqlToyDemoApplication {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
       Solon.start(SqlToyDemoApplication.class,args);
        FruitOrderService service=Aop.get(FruitOrderService.class);

        FruitOrderVO fruitOrderVO=new FruitOrderVO();
        fruitOrderVO.setFruitName("test");
        fruitOrderVO.setOrderMonth(1);
        fruitOrderVO.setSaleCount(new BigDecimal(2));
        fruitOrderVO.setSalePrice(new BigDecimal(1));
        fruitOrderVO.setTotalAmt(new BigDecimal(1));
        Page<FruitOrderVO> page= service.searchFruitOrder(new Page(),fruitOrderVO);
        System.out.println(page);
    }
}
