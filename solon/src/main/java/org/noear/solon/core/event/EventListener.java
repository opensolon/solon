package org.noear.solon.core.event;

/**
 * 事件监听者
 *
 * <pre><code>
 * //demo1: 在手动添加Listener
 * Solon.app().onEvent(Throwable.class, (err)->{
 *
 * });
 *
 * //demo2: 自动添加Listener
 * @Component
 * public class ThrowableListener implements EventListener<Throwable>{
 *     public void onEvent(Throwable err){
 *
 *     }
 * }
 *
 * //demo3: 自定义事件及监听（事件可以是任何对象）
 * //订阅事件
 * Solon.app().onEvent(ComAdaptor.class,(oc)->{ ... });
 *
 * //发布事件
 * EventBus.publish(new ComAdaptor());
 * </code></pre>
 * @author noear
 * @since 1.0
 * */
public interface EventListener<Event> {
    void onEvent(Event event) throws Throwable;
}
