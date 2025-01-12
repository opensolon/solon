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
package features;

import org.junit.jupiter.api.Test;
import org.noear.nami.Nami;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.nami.ComplexModel;
import webapp.nami.ComplexModelService1;
import webapp.nami.ComplexModelService2;
import webapp.nami.ComplexModelService3;

/**
 * @author noear 2022/12/6 created
 */
@SolonTest(App.class)
public class NamiTest {
    @NamiClient
    ComplexModelService1 complexModelService1;

    @NamiClient
    ComplexModelService2 complexModelService2;

    @NamiClient
    ComplexModelService3 complexModelService3;

    @Test
    public void test1() {
        ComplexModel model = new ComplexModel();
        model.setModelId(11);
        complexModelService1.save(model);

        assert complexModelService1.read(12).getModelId() == 12;
    }
    @Test
    public void test1b() {
        ComplexModel model = new ComplexModel();
        model.setModelId(11);
        String rst = complexModelService1.save2("noear",model);

        assert rst.contains("noear");
        assert rst.contains("11");
    }

    @Test
    public void test1_2() {
        ComplexModelService1 service1_2 = Nami.builder()
                .name("local")
                .path("/nami/ComplexModelService1/")
                .encoder(SnackEncoder.instance)
                .create(ComplexModelService1.class);

        assert service1_2.read(12).getModelId() == 12;
    }

    @Test
    public void test2() {
        ComplexModel model = new ComplexModel();
        model.setModelId(21);
        complexModelService2.save(model);

        assert complexModelService2.read(22).getModelId() == 22;
    }

    @Test
    public void test3() {
        ComplexModel model = new ComplexModel();
        model.setModelId(31);
        complexModelService3.save(model);

        assert complexModelService3.read(32).getModelId() == 32;
    }
}
