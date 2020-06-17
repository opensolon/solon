package feature.controller.apis;

public class ApiResult {
    public int code;
    public String message;
    public Object data;

    public ApiResult() {

    }

    public ApiResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResult(int code, String message, Object data) {
        this(code, message);
        this.data = data;
    }
}
