package com.sqltoy.helloworld;

import com.sqltoy.helloworld.vo.FruitOrderVO;
import org.noear.solon.extend.sqltoy.annotation.Sql;
import org.sagacity.sqltoy.model.Page;

public interface FruitOrderDao {
    /**
     * 分页查询订单信息
     *
     * @param pageModel
     * @param fruitOrderVO
     * @return
     */
    @Sql("helloworld_search_fruitOrder")
    Page<FruitOrderVO> searchFruitOrder(Page pageModel, FruitOrderVO fruitOrderVO);
}
