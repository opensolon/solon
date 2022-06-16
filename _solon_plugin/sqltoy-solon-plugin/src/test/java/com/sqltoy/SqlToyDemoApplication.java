package com.sqltoy;

import com.mongodb.client.MongoDatabase;
import com.sqltoy.helloworld.FruitMapper;
import com.sqltoy.helloworld.service.FruitOrderService;

import com.sqltoy.helloworld.vo.FruitOrderVO;
import org.bson.Document;
import org.noear.solon.Solon;

import org.noear.solon.core.Aop;
import org.noear.solon.extend.sqltoy.DbManager;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;
import org.sagacity.sqltoy.utils.BeanUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class SqlToyDemoApplication {
 public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
  Solon.start(SqlToyDemoApplication.class, args);


  FruitOrderService service = Aop.get(FruitOrderService.class);

  FruitOrderVO fruitOrderVO = new FruitOrderVO();
  fruitOrderVO.setFruitName("test");
  fruitOrderVO.setOrderMonth(1);
  fruitOrderVO.setSaleCount(new BigDecimal(2));
  fruitOrderVO.setSalePrice(new BigDecimal(1));
  fruitOrderVO.setTotalAmt(new BigDecimal(1));

  Page<FruitOrderVO> page = service.searchFruitOrder(new Page(), fruitOrderVO);
  System.out.println(page);

  SqlToyLazyDao dao = DbManager.getDao(Aop.get(DataSource.class));
  FruitMapper fm = DbManager.getMapper(Aop.get(DataSource.class), FruitMapper.class);
  System.out.println(fm.countOrder(null).getRows());
  FruitOrderVO fu = new FruitOrderVO();
  fu.setFruitName("test");
  System.out.println(fm.countOrder1(fu).getRows());
//  MongoDatabase db=Aop.get(MongoDatabase.class);
//  //db.createCollection("fact_trans_details");
//  Document doc=new Document("_id", new Random().nextInt(100));
//  doc.append("transType","test123");
//
//  db.getCollection("fact_trans_details").insertOne(doc);
//
//  List li= dao.mongo().sql("sqltoy_mongo_find").names("transType").values("test").resultType(Map.class).find();
//  System.out.println(li);
 }
}
