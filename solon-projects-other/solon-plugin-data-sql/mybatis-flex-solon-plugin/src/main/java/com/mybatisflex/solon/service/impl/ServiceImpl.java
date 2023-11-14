/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.solon.service.impl;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.service.IService;
import org.noear.solon.annotation.Inject;

/**
 * 默认 {@link IService} 实现类。
 *
 * @param <M> Mapper 类型
 * @param <T> Entity 类型
 */
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    @Inject
    protected M mapper;

    @Override
    public BaseMapper<T> getMapper() {
        return mapper;
    }

}