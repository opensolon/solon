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
package webapp.nami;

import lombok.extern.slf4j.Slf4j;
import org.noear.snack.ONode;
import org.noear.solon.annotation.*;

/**
 * 测试支持普通 Nami 和 Rpc Nami
 *
 * @author noear 2022/12/6 created
 */
@Slf4j
@Mapping("/nami/ComplexModelService1")
@Controller
public class ComplexModelService1Impl implements ComplexModelService1 {
    @Mapping("save")
    @Put
    @Override
    public void save(@Body ComplexModel model) {
        log.debug("{}", model);
    }

    @Mapping("save2")
    @Post
    @Override
    public String save2(String name1, @Body ComplexModel model) {
        return name1 + ONode.stringify(model);
    }

    @Mapping("read")
    @Override
    public ComplexModel read(Integer modelId) {
        ComplexModel model = new ComplexModel();
        model.setModelId(modelId);
        return model;
    }
}
