package server.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import server.dso.IComplexModelService;
import server.model.ComplexModel;

import java.util.HashMap;
import java.util.Map;

@XController
public class ComplexModelService implements IComplexModelService {
    private Map<Integer,ComplexModel> models = new HashMap<Integer, ComplexModel>();

    @XMapping()
    public void index() {

    }

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
