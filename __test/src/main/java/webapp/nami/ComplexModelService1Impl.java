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
