package server.dso;

import org.noear.nami.annotation.NamiClient;
import server.model.ComplexModel;
import server.model.Point;

@NamiClient("test:/ComplexModelService/")
public interface IComplexModelService {
    //持久化
    void save(ComplexModel<Point> model);

    //读取
    ComplexModel read(Integer modelId);
}
