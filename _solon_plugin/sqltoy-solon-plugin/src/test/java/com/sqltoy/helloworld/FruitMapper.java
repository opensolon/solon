package com.sqltoy.helloworld;

import com.sqltoy.helloworld.vo.FruitOrderVO;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.extend.sqltoy.annotation.Param;
import org.noear.solon.extend.sqltoy.annotation.Sql;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;

/**
 * Mapper说明：
 * 在@Sql中写 sqltoy Sql或者 sqlId
 * 没有@Sql注解时 通过方法名映射sqlId
 * 返回值分为Page,List,Entity和直接值(如：Integer等)，当其中Page,List的类型为直接值或返回值类型为直接值时,具体类型由sql中查询结果决定
 * 参数可有0～2个,两个参数时其中一个为Page,另外一个为Map或Entity,顺序不限
 * 当为default方法时，直接调用default方法
 *
 * @deprecated Mapper实现已移除
 */
public interface FruitMapper {
    //通过注解映射
    @Sql("helloworld_search_fruitOrder")
    Page<FruitOrderVO> findOrder(Page page, FruitOrderVO params);
    //通过方法名映射
    Page<FruitOrderVO> helloworld_search_fruitOrder(Page page, FruitOrderVO params);
    //直接写sql
    @Sql("select TOTAL_AMT from sqltoy_fruit_order where #[FRUIT_NAME = :fruitName]")
    Page<String> countOrder(String fruitName);
    @Sql("select TOTAL_AMT from sqltoy_fruit_order where #[FRUIT_NAME in (:names)]")
    Page<String> allIn(String[] names);
    @Sql("select TOTAL_AMT from sqltoy_fruit_order where #[FRUIT_NAME = :fruit.fruitName]")
    Page<String> countOrder1(@Param FruitOrderVO fruit);

    /**
     * 可获取默认的dao,加入任意名字的String类型参数，可获取指定数据源名称的dao
     * @return
     */
    SqlToyLazyDao dao();

}
