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
package demo.sqlink.model;

import demo.sqlink.interceptor.Decryption;
import demo.sqlink.interceptor.Encryption;
import org.noear.solon.data.sqlink.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Table("user")
public class User {
    // 主键
    @Column(primaryKey = true)
    // 插入时默认值，来自数据库
    @InsertDefaultValue(strategy = GenerateStrategy.DataBase)
    private long id;
    private String username;
    //加解密密码
    @OnPut(Encryption.class)
    @OnGet(Decryption.class)
    private String password;
    private String email;
    @Column("update_time")
    // 插入与更新时默认值，来自数据库
    @InsertDefaultValue(strategy = GenerateStrategy.DataBase)
    private LocalDateTime updateTime;
    private List<Area> areas;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }
}
