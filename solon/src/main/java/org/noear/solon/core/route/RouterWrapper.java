package org.noear.solon.core.route;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.ListenerPipeline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 路由包装器（更简单的使用路由）
 *
 * @author noear
 * @since 1.8
 */
public abstract class RouterWrapper implements HandlerSlots{
    private Router _router;
    private RouterHandler _routerHandler;
    private List<FilterEntity> _filterList = new ArrayList<>();

    public abstract AopContext context();

    protected void initRouter(Filter appFilter){
        //顺序不能换
        _router = new RouterDefault();
        _routerHandler = new RouterHandler(_router);
        _filterList.add(new FilterEntity(Integer.MAX_VALUE, appFilter));
    }


    protected RouterHandler routerHandler(){
        return _routerHandler;
    }

    protected List<FilterEntity> filterList(){
        return _filterList;
    }

    /**
     * 路由器
     */
    public Router router() {
        return _router;
    }

    public void routerSet(Router router) {
        if (router != null) {
            _router = router;
            _routerHandler.bind(router);
        }
    }


    ///////////////////////////////////////////////
    //
    // 以下为web handler 有关
    //
    //////////////////////////////////////////////

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param filter 过滤器
     */
    public void filter(Filter filter) {
        filter(0, filter);
    }

    /**
     * 添加过滤器（按先进后出策略执行）
     *
     * @param index  顺序位
     * @param filter 过滤器
     * @since 1.5
     */
    public void filter(int index, Filter filter) {
        _filterList.add(new FilterEntity(index, filter));
        _filterList.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * 添加前置处理
     */
    public void before(Handler handler) {
        before("**", MethodType.ALL, handler);
    }

    /**
     * 添加前置处理
     */
    public void before(int index, Handler handler) {
        before("**", MethodType.ALL, index, handler);
    }

    /**
     * 添加前置处理
     *
     * @since 1.6
     */
    public void before(MethodType method, Handler handler) {
        before("**", method, handler);
    }

    /**
     * 添加前置处理
     *
     * @since 1.6
     */
    public void before(MethodType method, int index, Handler handler) {
        before("**", method, index, handler);
    }

    /**
     * 添加前置处理
     */
    public void before(String expr, Handler handler) {
        before(expr, MethodType.ALL, handler);
    }

    /**
     * 添加前置处理
     */
    public void before(String expr, MethodType method, Handler handler) {
        _router.add(expr, Endpoint.before, method, handler);
    }

    /**
     * 添加前置处理
     */
    @Override
    public void before(String expr, MethodType method, int index, Handler handler) {
        _router.add(expr, Endpoint.before, method, index, handler);
    }

    /**
     * 添加后置处理
     */
    public void after(Handler handler) {
        after("**", MethodType.ALL, handler);
    }

    /**
     * 添加后置处理
     *
     * @since 1.6
     */
    public void after(MethodType method, Handler handler) {
        after("**", method, handler);
    }

    /**
     * 添加后置处理
     *
     * @since 1.6
     */
    public void after(String expr, Handler handler) {
        after(expr, MethodType.ALL, handler);
    }

    /**
     * 添加后置处理
     */
    public void after(String expr, MethodType method, Handler handler) {
        _router.add(expr, Endpoint.after, method, handler);
    }

    /**
     * 添加后置处理
     */
    @Override
    public void after(String expr, MethodType method, int index, Handler handler) {
        _router.add(expr, Endpoint.after, method, index, handler);
    }

    /**
     * 添加主体处理
     */
    @Override
    public void add(String expr, MethodType method, Handler handler) {
        _router.add(expr, Endpoint.main, method, handler);
    }

    public void add(String expr, Class<?> clz) {
        BeanWrap bw = context().wrapAndPut(clz);
        if (bw != null) {
            new HandlerLoader(bw, expr).load(this);
        }
    }

    public void add(String expr, Class<?> clz, boolean remoting) {
        BeanWrap bw = context().wrapAndPut(clz);
        if (bw != null) {
            new HandlerLoader(bw, expr, remoting).load(this);
        }
    }


    /**
     * 添加所有方法处理
     */
    public void all(String path, Handler handler) {
        add(path, MethodType.ALL, handler);
    }

    /**
     * 添加HTTP所有方法的处理（GET,POST,PUT,PATCH,DELETE,HEAD）
     */
    public void http(String path, Handler handler) {
        add(path, MethodType.HTTP, handler);
    }

    /**
     * 添加HEAD方法的处理
     */
    public void head(String path, Handler handler) {
        add(path, MethodType.HEAD, handler);
    }

    /**
     * 添加GET方法的处理（REST.select 从服务端获取一或多项资源）
     */
    public void get(String path, Handler handler) {
        add(path, MethodType.GET, handler);
    }

    /**
     * 添加POST方法的处理（REST.create 在服务端新建一项资源）
     */
    public void post(String path, Handler handler) {
        add(path, MethodType.POST, handler);
    }

    /**
     * 添加PUT方法的处理（REST.update 客户端提供改变后的完整资源）
     */
    public void put(String path, Handler handler) {
        add(path, MethodType.PUT, handler);
    }

    /**
     * 添加PATCH方法的处理（REST.update 客户端提供改变的属性）
     */
    public void patch(String path, Handler handler) {
        add(path, MethodType.PATCH, handler);
    }

    /**
     * 添加DELETE方法的处理（REST.delete 从服务端删除资源）
     */
    public void delete(String path, Handler handler) {
        add(path, MethodType.DELETE, handler);
    }

    /**
     * 添加web socket方法的监听
     */
    public void ws(String path, Handler handler) {
        add(path, MethodType.WEBSOCKET, handler);
    }

    /**
     * 添加web socket方法的监听
     */
    public void ws(String path, Listener listener) {
        _router.add(path, MethodType.WEBSOCKET, listener);
    }

    /**
     * 添加socket方法的监听
     */
    public void socket(String path, Handler handler) {
        add(path, MethodType.SOCKET, handler);
    }

    /**
     * 添加socket方法的监听
     */
    public void socket(String path, Listener listener) {
        _router.add(path, MethodType.SOCKET, listener);
    }

    /**
     * 添加监听
     */
    public void listen(String path, Listener listener) {
        _router.add(path, MethodType.ALL, listener);
    }

    /**
     * 添加监听到之前的位置
     * */
    public void listenBefore(Listener listener){
        _listenerPipeline.prev(listener);
    }

    /**
     * 添加监听到之后的位置
     * */
    public void listenAfter(Listener listener){
        _listenerPipeline.next(listener);
    }

    private final ListenerPipeline _listenerPipeline = new ListenerPipeline();
    /**
     * 监听器入口
     * */
    public Listener listener(){
        return _listenerPipeline;
    }
}
