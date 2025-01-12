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
package org.noear.solon.auth;

import org.noear.solon.core.handle.Handler;

/**
 * 授权规则
 *
 * @author noear
 * @since 1.4
 */
public interface AuthRule extends Handler {
    /**
     * 包函
     *
     * @param pathPattern 路径模式
     */
    AuthRule include(String pathPattern);

    /**
     * 排除
     *
     * @param pathPattern 路径模式
     */
    AuthRule exclude(String pathPattern);

    /**
     * 验证Ip
     */
    AuthRule verifyIp();

    /**
     * 验证登录状态
     */
    AuthRule verifyLogined();

    /**
     * 验证路径
     */
    AuthRule verifyPath();

    /**
     * 验证权限
     *
     * @param permissions 权限
     */
    AuthRule verifyPermissions(String... permissions);

    /**
     * 验证权限（并且关系）
     *
     * @param permissions 权限
     */
    AuthRule verifyPermissionsAnd(String... permissions);

    /**
     * 验证角色
     *
     * @param roles 角色
     */
    AuthRule verifyRoles(String... roles);

    /**
     * 验证角色（并且关系）
     *
     * @param roles 角色
     */
    AuthRule verifyRolesAnd(String... roles);

    /**
     * 失败事件
     */
    AuthRule failure(AuthFailureHandler handler);
}
