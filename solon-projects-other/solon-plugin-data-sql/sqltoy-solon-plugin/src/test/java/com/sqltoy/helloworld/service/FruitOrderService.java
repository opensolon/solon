package com.sqltoy.helloworld.service;


import org.noear.solon.data.annotation.Tran;
import org.noear.solon.extend.sqltoy.annotation.Db;
import org.noear.solon.annotation.Component;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import com.sqltoy.helloworld.vo.FruitOrderVO;

/**
 * 水果订单服务
 *
 * @author zhongxuchen
 * @date 2021/4/19
 */
@Component
public class FruitOrderService {
	// sqltoy只要注入框架自带的sqlToyLazyDao 就可以通过.xxx 完成全部数据库操作，具体有哪些功能可以参见SqlToyLazyDao
	// 功能定义
	@Db
	private SqlToyLazyDao sqlToyLazyDao;
	@Db("tow")
	private SqlToyLazyDao sqlToyLazyDao2;

	@Tran
	public void createFruitOrder(FruitOrderVO fruitOrderVO) {
		sqlToyLazyDao.save(fruitOrderVO);
	}
}
