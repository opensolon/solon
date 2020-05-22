package org.noear.solon.extend.uapi;

import org.noear.solon.core.Result;
import org.noear.solon.core.XContext;

public abstract class UApiBase<P,R> implements UApi {

    @Override
    public Object call(XContext cxt) {
        Result<R> result = new Result<>();
        return null;
    }

    public abstract void exec(P paramS, Result<R> result);
}
