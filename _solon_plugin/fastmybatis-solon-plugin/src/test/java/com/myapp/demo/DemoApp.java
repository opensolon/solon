package com.myapp.demo;

import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.util.MapperUtil;
import com.myapp.demo.dao.TUserMapper;
import com.myapp.demo.model.TUser;
import com.myapp.demo.model.UserInfoDO;
import org.junit.Assert;
import org.junit.Test;
import org.noear.solon.Solon;

import java.util.List;


// --------------------------
// 数据库脚本

/*
CREATE
DATABASE IF NOT EXISTS `stu` DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
use
stu;

DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`   varchar(255)   DEFAULT NULL COMMENT '用户名',
    `state`      tinyint(4) DEFAULT NULL COMMENT '状态',
    `isdel`      int(11) DEFAULT NULL COMMENT '是否删除',
    `remark`     text COMMENT '备注',
    `add_time`   datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `money`      decimal(10, 2) DEFAULT NULL COMMENT '金额',
    `left_money` float          DEFAULT NULL COMMENT '剩下的钱',
    PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='用户表';



INSERT INTO `t_user`(`id`, `username`, `state`, `isdel`, `remark`, `add_time`, `money`, `left_money`)
VALUES (1, '王五', 0, 0, '批量修改备注', '2017-02-21 10:37:44', '101.10', 22.1),
       (2, '张三', 0, 0, '批量修改备注', '2017-02-21 10:40:11', '100.50', 22.1),
       (3, '张三', 1, 0, '备注', '2017-02-21 10:40:11', '100.50', 22.1),
       (4, '张三', 0, 0, '批量修改备注', '2017-02-21 10:40:11', '100.50', 22.1),
       (5, '张三', 0, 0, '批量修改备注', '2017-02-21 10:40:11', '100.50', 22.1),
       (6, '张三', 0, 0, '批量修改备注', '2017-02-21 10:40:11', '100.50', 22.1);


DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id`     int(11) NOT NULL COMMENT 't_user外键',
    `city`        varchar(50)        DEFAULT NULL COMMENT '城市',
    `address`     varchar(100)       DEFAULT NULL COMMENT '街道',
    `status`      varchar(4)         DEFAULT '0' COMMENT '类型',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

INSERT INTO `stu`.`user_info` (`user_id`,
                               `city`,
                               `address`)
SELECT t.id
     , '杭州'
     , '延安路'
FROM t_user t;
 */
// --------------------------

/**
 * @author noear 2021/7/12 created
 */
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);

        //test
        TUserMapper tUserMapper = Solon.context().getBean(TUserMapper.class);
        Assert.assertNotNull(tUserMapper);
        TUser user = tUserMapper.getById(6);
        System.out.println(user);
        Assert.assertNotNull(user);

        joinPage();
    }

    /**
     * 演示联表查询且分页
     */
    public static void joinPage() {
        TUserMapper tUserMapper = Solon.context().getBean(TUserMapper.class);

        Query query = new Query()
                // 联表查询需要带上表别名t.
                .gt("t.id", 1)
                .eq("t2.city", "杭州")
                .page(1, 10);

        // 分页查询只需要返回总数，以及当前分页内容
        PageInfo<UserInfoDO> pageInfo = MapperUtil.query(query, tUserMapper::getUserInfoCount, tUserMapper::listUserInfo);

        List<UserInfoDO> list = pageInfo.getList(); // 结果集
        long total = pageInfo.getTotal(); // 总记录数
        int pageCount = pageInfo.getPageCount(); // 共几页

        System.out.println("total:" + total);
        System.out.println("pageCount:" + pageCount);
        list.forEach(System.out::println);
    }

}
