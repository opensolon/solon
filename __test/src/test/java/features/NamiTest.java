package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.nami.Nami;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.nami.ComplexModel;
import webapp.nami.ComplexModelService1;
import webapp.nami.ComplexModelService2;
import webapp.nami.ComplexModelService3;

/**
 * @author noear 2022/12/6 created
 */
@ExtendWith(SolonJUnit5Extension.class)
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
