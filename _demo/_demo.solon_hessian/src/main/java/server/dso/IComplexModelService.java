package server.dso;

import server.model.ComplexModel;

public interface IComplexModelService {
    //持久化
    void save(ComplexModel model);

    //读取
    ComplexModel read(Integer modelId);


}
