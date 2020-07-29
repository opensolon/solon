package server.dso;

import server.model.ComplexModel;

public interface IComplexModelService {
    //持久化
    public void save(ComplexModel model);

    //读取
    public ComplexModel read(Integer modelId);
}
