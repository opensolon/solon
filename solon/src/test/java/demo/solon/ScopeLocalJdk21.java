package demo.solon;

import org.noear.solon.core.util.CallableTx;
import org.noear.solon.core.util.ScopeLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.lang.ScopedValue;
//
//public class ScopeLocalJdk21<T> implements ScopeLocal<T> {
//    private static final Logger log = LoggerFactory.getLogger(ScopeLocalJdk21.class);
//    private final ScopedValue<T> ref = ScopedValue.newInstance();
//
//    @Override
//    public T get() {
//        return ref.get();
//    }
//
//    @Override
//    public void with(T value, Runnable runnable) {
//        ref.where(ref, value).run(runnable);
//    }
//
//    @Override
//    public <R, X extends Throwable> R with(T value, CallableTx<? extends R, X> callable) throws X {
//        return ref.where(ref, value).call(callable::call);
//    }
//
//    @Override
//    public ScopeLocal<T> set(T value) {
//        log.error("ScopeLocal.set is invalid, please use ScopeLocal.with");
//        return null;
//    }
//
//    @Override
//    public void remove() {
//        log.error("ScopeLocal.remove is invalid, please use ScopeLocal.with");
//    }
//}
