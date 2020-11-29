package server.dso;

import org.noear.nami.annotation.NamiClient;
import server.model.ComplexModel;

@NamiClient("test:/ComplexModelService/")
public interface IComplexModelService {
    //持久化
    void save(ComplexModel model);

    //读取
    ComplexModel read(Integer modelId);
}
