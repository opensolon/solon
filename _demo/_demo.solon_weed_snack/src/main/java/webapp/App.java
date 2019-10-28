package webapp;

import org.noear.solon.XApp;
import org.noear.weed.cache.ICacheServiceEx;
import org.noear.weed.cache.LocalCache;

public class App {
    public static void main(String[] args){

        ICacheServiceEx cache = new LocalCache("test",60).nameSet("test");

        XApp app = XApp.start(App.class,args);

        app.get("/",(c)->{
            c.render("nav.htm", null);
        });

        /*
        *  测试用到的表结构
   CREATE TABLE `appx` (
  `app_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '应用ID',
  `app_key` varchar(40) DEFAULT NULL COMMENT '应用访问KEY',
  `akey` varchar(40) DEFAULT NULL COMMENT '（用于取代app id 形成的唯一key） //一般用于推广注册之类',
  `ugroup_id` int(11) DEFAULT '0' COMMENT '加入的用户组ID',
  `agroup_id` int(11) DEFAULT NULL COMMENT '加入的应用组ID',
  `name` varchar(50) DEFAULT NULL COMMENT '应用名称',
  `note` varchar(50) DEFAULT NULL COMMENT '应用备注',
  `ar_is_setting` int(1) NOT NULL DEFAULT '0' COMMENT '是否开放设置',
  `ar_is_examine` int(1) NOT NULL DEFAULT '0' COMMENT '是否审核中(0: 没审核 ；1：审核中)',
  `ar_examine_ver` int(11) NOT NULL DEFAULT '0' COMMENT '审核 中的版本号',
  `log_fulltime` datetime DEFAULT NULL,
  PRIMARY KEY (`app_id`),
  UNIQUE KEY `IX_akey` (`akey`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';
        *
        * */
    }
}
