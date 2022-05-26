package com.sqltoy.helloworld;

import com.sqltoy.helloworld.vo.FruitOrderVO;
import org.noear.solon.extend.sqltoy.annotation.Sql;
import org.sagacity.sqltoy.model.Page;

/**
 * Mapper说明：
 * 在@Sql中写 sqltoy Sql或者 sqlId
 * 没有@Sql注解时 通过方法名映射sqlId
 * 返回值分为Page,List,Entity和直接值(如：Integer等)，当其中Page,List的类型为直接值或返回值类型为直接值时,具体类型由sql中查询结果决定
 * 参数可有0～2个,两个参数时其中一个为Page,另外一个为Map或Entity,顺序不限
 * 当为default方法时，直接调用default方法
 */
public interface FruitMapper {
    //通过注解映射
    @Sql("helloworld_search_fruitOrder")
    Page<FruitOrderVO> findOrder(Page page, FruitOrderVO params);
    //通过方法名映射
    Page<FruitOrderVO> helloworld_search_fruitOrder(Page page, FruitOrderVO params);
    //直接写sql
    @Sql("select TOTAL_AMT from sqltoy_fruit_order where #[TOTAL_AMT = :totalAmt]")
    Page<Long> countOrder(FruitOrderVO params);

}
