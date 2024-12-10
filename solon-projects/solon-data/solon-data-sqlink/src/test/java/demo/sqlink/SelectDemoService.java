/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.sqlink;

import demo.sqlink.model.User;
import demo.sqlink.vo.UserVo;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;
import org.noear.solon.data.sqlink.api.Result;
import org.noear.solon.data.sqlink.core.sqlExt.SqlFunctions;

import java.util.List;

import static org.noear.solon.data.sqlink.core.SubQuery.subQuery;

@Component
public class SelectDemoService {
    @Inject // or @Inject("main")
    SqLink sqLink;

    public String helloWorld(String value) {
        return sqLink.queryEmptyTable()
                // CONCAT_WS(' ','hello',{value})
                .endSelect(() -> SqlFunctions.join(" ", "hello", value))
                .first();
    }

    // 统计email不为空的用户数量
    public long findEmailNotNullUserCount() {
        return sqLink.query(User.class).count(u -> u.getEmail() != null);
    }

    // 根据id查询用户
    public User findById(long id) {
        return sqLink.query(User.class)
                .where(user -> user.getId() == id)
                .first();
    }

    // 根据名称和email查询一位用户
    public User findByNameAndEmail(String name, String email) {
        return sqLink.query(User.class)
                .where(u -> u.getUsername() == name && u.getEmail() == email)
                .first();
    }

    // 根据名称查询模糊匹配用户
    public List<User> findByName(String name) {
        return sqLink.query(User.class)
                // username LIKE '{name}%'
                .where(u -> u.getUsername().startsWith(name))
                .toList();
    }

    // 根据名称查询模糊匹配用户, 并且以匿名对象形式返回我们感兴趣的数据
    public List<? extends Result> findResultByName(String name) {
        return sqLink.query(User.class)
                // username LIKE '{name}%'
                .where(u -> u.getUsername().startsWith(name))
                .select(u -> new Result() {
                    long id = u.getId();
                    String email = u.getEmail();
                }).toList();
    }

    // 或者使用Vo返回
    public List<UserVo> findUserVoByName(String name) {
        return sqLink.query(User.class)
                .where(u -> u.getUsername().startsWith(name))
                .select(UserVo.class).toList();
    }

    // 根据地区码查询用户
    public List<User> findUserByAreaCode(String code) {
        return sqLink.query(User.class)
                .where(u -> subQuery(u.getAreas()).any(a -> a.getCode() == code))
                .toList();
    }

    // 查询用户与他的前五个地址
    public List<User> findUserByHasNotAreaCode(int id) {
        int count = 1000;
        return sqLink.query(User.class)
                .includes(user -> user.getAreas(), areas -> areas.limit(5))
                .toList();
    }

//    public List<User> lowerSql(int id) {
//        return sqLink.execQuery(
//                User.class,
//                value -> MessageFormat.format("select * from user where id = {0} and name = {1}", value.iid, value.name),
//                new SqlValues() {
//                    int iid = id;
//                    String name = "name";
//                });
//    }
}
