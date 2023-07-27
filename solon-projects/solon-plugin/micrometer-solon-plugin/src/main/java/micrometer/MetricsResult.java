package micrometer;

/**
 * 度量结果
 *
 * @author bai
 * @date 2023/07/27
 */
public class MetricsResult<T> {

    /**
     * 代码
     */
    int code;

    /**
     * 消息
     */
    String message;

    /**
     * 数据
     */
    T data;

    /**
     * 度量结果
     *
     * @param code    代码
     * @param message 消息
     * @param data    数据
     */
    public MetricsResult(int code, String message , T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 好吧
     *
     * @param data 数据
     * @return {@link MetricsResult}<{@link T}>
     */
    public static<T> MetricsResult<T> ok(T data){
        return new MetricsResult<>(200, "ok", data);
    }
}


