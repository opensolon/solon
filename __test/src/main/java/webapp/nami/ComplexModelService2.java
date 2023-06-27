package webapp.nami;


import org.noear.nami.annotation.NamiBody;
import org.noear.nami.annotation.NamiMapping;
import org.noear.nami.annotation.NamiClient;

/**
 * @author noear 2022/12/6 created
 */
@NamiClient(name="local", path="/nami/ComplexModelService2/", headers="TOKEN=a")
public interface ComplexModelService2 {
    @NamiMapping("PUT")
    void save(@NamiBody ComplexModel model);

    @NamiMapping("GET api/1.0.1")
    ComplexModel read(Integer modelId);
}
