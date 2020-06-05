package org.noear.solon.extend.uapi;

import org.noear.solon.core.*;

/**
 * UAPI
 * */
public abstract class UApi extends XCallable {
    public UApi(){
        super();
    }

    /**
     * 接口名（即：代号）
     *
     * 配合UApiNav时,必须重写
     * */
    public String name(){ return null; }

    @Override
    public void handle(XContext x) throws Throwable {
        super.handle(x);
    }

    /** 改写渲染机制 */
    @Override
    protected void innerRender(XContext x, Object result) throws Throwable {
        if(result == null){
            return;
        }

        //_uapinav 由 UApiNav 写入
        //
        if(x.attr("_uapinav",null) == null){
            x.render(result);
        }else {
            if (result instanceof Throwable) {
                x.attrSet("result", new Result<>(0, null, result));
            } else {
                x.attrSet("result", result);
            }
        }
    }
}
