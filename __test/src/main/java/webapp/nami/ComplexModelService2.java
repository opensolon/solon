package webapp.nami;


import org.noear.nami.annotation.Body;
import org.noear.nami.annotation.Mapping;
import org.noear.nami.annotation.NamiClient;

/**
 * @author noear 2022/12/6 created
 */
@NamiClient(name="local", path="/nami/ComplexModelService2/", headers="TOKEN=a")
public interface ComplexModelService2 {
    @Mapping("PUT")
    void save(@Body ComplexModel model);

    @Mapping("GET api/1.0.1")
    ComplexModel read(Integer modelId);
}
