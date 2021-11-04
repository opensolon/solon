package com.sqltoy.helloworld.service;

import com.sqltoy.helloworld.FruitMapper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.extend.aspect.annotation.Service;
import org.noear.solon.extend.sqltoy.annotation.Mapper;
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
	@Inject
	private SqlToyLazyDao sqlToyLazyDao;
	@Inject
	private FruitMapper fruitMapper;
	@Tran
	public void createFruitOrder(FruitOrderVO fruitOrderVO) {
		sqlToyLazyDao.save(fruitOrderVO);
	}


	public Page<FruitOrderVO> searchFruitOrder(Page pageModel, FruitOrderVO fruitOrderVO) {

		return fruitMapper.findOrder(pageModel,fruitOrderVO);
	}
}
