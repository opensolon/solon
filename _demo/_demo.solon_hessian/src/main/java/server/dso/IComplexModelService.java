package server.dso;

import org.noear.fairy.annotation.FairyClient;
import server.model.ComplexModel;

@FairyClient("test:/ComplexModelService/")
public interface IComplexModelService {
    //持久化
    void save(ComplexModel model);

    //读取
    ComplexModel read(Integer modelId);
}
