package server.dso;

import org.noear.solonclient.annotation.XClient;
import server.model.ComplexModel;

@XClient("test:/ComplexModelService/")
public interface IComplexModelService {
    //持久化
    void save(ComplexModel model);

    //读取
    ComplexModel read(Integer modelId);


}
