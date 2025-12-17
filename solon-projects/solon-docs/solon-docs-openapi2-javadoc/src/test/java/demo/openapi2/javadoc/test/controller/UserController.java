/*
 * Copyright 2017-2025 noear.org and authors
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
package demo.openapi2.javadoc.test.controller;

import demo.openapi2.javadoc.test.domain.Page;
import demo.openapi2.javadoc.test.domain.PageImpl;
import demo.openapi2.javadoc.test.domain.bo.UserBo;
import demo.openapi2.javadoc.test.domain.vo.UserVo;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Result;

import java.util.List;

/**
 * 用户信息表 控制器
 *
 * @author chengliang4810
 * @since 2024-04-08
 */
@Controller
@Mapping("/test/user")
public class UserController {
    /**
     * 查询用户列表
     *
     * @param userBo 用户bo
     * @return {@link List}<{@link UserVo}>
     */
    @Get
    @Mapping("/list")
    public List<UserVo> list(UserBo userBo) {
        return null;
    }

    @Get
    @Mapping("/page")
    public Page page() {
        return new PageImpl(1, 1);
    }

    /**
     * 根据Id查询用户信息
     *
     * @param id 身份证件
     * @return {@link UserVo}
     */
    @Get
    @Mapping("/{id}")
    public UserVo get(Long id) {
        return new UserVo();
    }

    /**
     * 根据Id查询用户信息2
     *
     * @param id 身份证件
     * @return {@link UserVo}
     */
    @Get
    @Mapping("/info/{id}")
    public Result<UserVo> info(Long id) {
        return Result.succeed(new UserVo());
    }

    /**
     * 新增用户信息
     *
     * @param userBo 用户bo
     * @return boolean
     */
    @Post
    @Mapping
    public boolean save(UserBo userBo) {
        return true;
    }

    /**
     * 根据Id更新用户信息
     *
     * @param userBo 用户bo
     * @return boolean
     */
    @Put
    @Mapping
    public boolean update(UserBo userBo) {
        return true;
    }

    /**
     * 删除用户信息
     *
     * @param ids ids
     * @return boolean
     */
    @Delete
    @Mapping("/{ids}")
    public boolean delete(String ids) {
        return true;
    }

}