package features.smarthttp.websocket;

/**
 * 线程工具类
 *
 * @author jaime
 * @version 1.0
 * @since 2025/1/21
 */
public class ThreadUtil {

    /**
     * 判断是否是虚拟线程
     *
     * @return boolean
     */
    public static boolean isVirtualThread() {
        try {
            Class<?> v = Class.forName("java.lang.BaseVirtualThread");
            Thread currentThread = Thread.currentThread();
            return v.isAssignableFrom(currentThread.getClass());
        } catch (Exception e) {
            return false;
        }
    }
}
