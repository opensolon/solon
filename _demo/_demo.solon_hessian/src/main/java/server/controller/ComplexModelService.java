package server.controller;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.MethodType;
import server.dso.IComplexModelService;
import server.model.ComplexModel;

import java.util.HashMap;
import java.util.Map;

@Mapping(value = "/ComplexModelService/",method = {MethodType.ALL})
@Remoting
public class ComplexModelService implements IComplexModelService {
    private Map<Integer,ComplexModel> models = new HashMap<Integer, ComplexModel>();


    @Override
    public void save(ComplexModel model) {
        if (model.getId() == null){
            throw new IllegalArgumentException("id could not be null");
        }

        models.put(model.getId(), model);
    }

    @Override
    public ComplexModel read(Integer modelId) {
        return models.get(modelId);
    }
}
