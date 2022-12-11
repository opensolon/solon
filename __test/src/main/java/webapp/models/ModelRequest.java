package webapp.models;

/**
 * @author noear 2022/12/10 created
 */
public class ModelRequest<T> extends BaseRequest {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
