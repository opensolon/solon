package webapp.nami;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.*;

/**
 * @author noear 2022/12/6 created
 */
@Slf4j
@Mapping("/nami/ComplexModelService1")
@Remoting
public class ComplexModelService1Impl implements ComplexModelService1 {
    @Put
    @Override
    public void save(@Body ComplexModel model) {
        log.debug("{}", model);
    }

    @Override
    public ComplexModel read(Integer modelId) {
        ComplexModel model = new ComplexModel();
        model.setModelId(modelId);
        return model;
    }
}
