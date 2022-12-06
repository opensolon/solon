package webapp.nami;

/**
 * @author noear 2022/12/6 created
 */
public class ComplexModel {
    private Integer modelId;

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    @Override
    public String toString() {
        return "ComplexModel{" +
                "modelId=" + modelId +
                '}';
    }
}
