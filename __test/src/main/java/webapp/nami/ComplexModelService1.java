package webapp.nami;


import org.noear.nami.annotation.NamiBody;
import org.noear.nami.annotation.NamiMapping;
import org.noear.nami.annotation.NamiClient;

/**
 * @author noear 2022/12/6 created
 */
@NamiClient(name="local", path="/nami/ComplexModelService1/")
public interface ComplexModelService1 {
    @NamiMapping("PUT")
    void save(@NamiBody ComplexModel model);

    ComplexModel read(Integer modelId);
}
