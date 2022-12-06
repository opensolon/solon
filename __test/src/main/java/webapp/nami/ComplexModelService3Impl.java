package webapp.nami;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.*;

/**
 * @author noear 2022/12/6 created
 */
@Slf4j
@Mapping("/nami/ComplexModelService3")
@Remoting
public class ComplexModelService3Impl implements ComplexModelService3 {
    @Put
    @Override
    public void save(@Body ComplexModel model) {
        log.debug("{}", model);
    }

    @Get
    @Mapping("api/1.0.1")
    @Override
    public ComplexModel read(Integer modelId) {
        ComplexModel model = new ComplexModel();
        model.setModelId(modelId);
        return model;
    }
}
