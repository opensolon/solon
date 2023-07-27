package micrometer;

/**
 * 通用注册表
 *
 * @author bai
 * @date 2023/07/27
 */
@FunctionalInterface
public interface CommonMeterRegistry<T> {


    /**
     * 注册表
     *
     * @param meterRegistry 计注册表
     */
    void registry(T meterRegistry);

}
