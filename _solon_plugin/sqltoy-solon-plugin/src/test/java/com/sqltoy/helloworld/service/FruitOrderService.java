package com.sqltoy.helloworld.service;


import org.noear.solon.data.annotation.Tran;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.extend.sqltoy.annotation.Db;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;
import com.sqltoy.helloworld.vo.FruitOrderVO;

/**
 * 水果订单服务
 *
 * @author zhongxuchen
 * @date 2021/4/19
 */
@Service
public class FruitOrderService {
	// sqltoy只要注入框架自带的sqlToyLazyDao 就可以通过.xxx 完成全部数据库操作，具体有哪些功能可以参见SqlToyLazyDao
	// 功能定义
	@Db
	private SqlToyLazyDao sqlToyLazyDao;
	@Db("tow")
	private SqlToyLazyDao sqlToyLazyDao2;
//	@Db
//	private FruitMapper fruitMapper;
//	@Db("tow")
//	private FruitMapper fruitMapper2;
	@Tran
	public void createFruitOrder(FruitOrderVO fruitOrderVO) {
		sqlToyLazyDao.save(fruitOrderVO);
	}


//	@Tran
//	public Page<FruitOrderVO> searchFruitOrder(Page pageModel, FruitOrderVO fruitOrderVO) {
//
//		/** 测试数据回滚
//		Map p=new HashMap();
//		sqlToyLazyDao2.executeSql("insert into test(name) value('haha2')",p);
//		sqlToyLazyDao.executeSql("insert into test(name) value('haha1')",p);
//		System.out.println(1/0);
//		 **/
//		//mapper多数据源, fruitMapper2.findOrder(pageModel,fruitOrderVO);
//		//return fruitMapper.findOrder(pageModel,fruitOrderVO);
//	}
}
