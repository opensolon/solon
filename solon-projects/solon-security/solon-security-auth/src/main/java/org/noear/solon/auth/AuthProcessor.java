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

import org.noear.solon.auth.annotation.Logical;

/**
 * 认证处理器（由用户对接）
 *
 * @author noear
 * @since 1.3
 */
public interface AuthProcessor {

    /**
     * 验证IP
     * */
    boolean verifyIp(String ip);

    /**
     * 验证登录状态
     */
    boolean verifyLogined();

    /**
     * 验证路径（一般使用路径验证）
     *
     * @param path 路径
     * @param method 请求方式
     */
    boolean verifyPath(String path, String method);

    /**
     * 验证特定权限（有特殊情况用权限验证）
     *
     * @param permissions 权限
     * @param logical 认证的逻辑关系
     */
    boolean verifyPermissions(String[] permissions, Logical logical);

    /**
     * 验证特定角色（有特殊情况用角色验证）
     *
     * @param roles 角色
     * @param logical 认证的逻辑关系
     */
    boolean verifyRoles(String[] roles, Logical logical);
}
