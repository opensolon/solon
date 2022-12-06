package webapp.nami;


import org.noear.nami.annotation.Body;
import org.noear.nami.annotation.Mapping;
import org.noear.nami.annotation.NamiClient;

/**
 * @author noear 2022/12/6 created
 */
@NamiClient(name="local", path="/nami/ComplexModelService1/")
public interface ComplexModelService1 {
    @Mapping("PUT")
    void save(@Body ComplexModel model);

    ComplexModel read(Integer modelId);
}
