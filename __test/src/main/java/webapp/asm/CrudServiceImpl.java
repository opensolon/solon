package webapp.asm;

import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/9/30 created
 */
public abstract class CrudServiceImpl<M extends BaseMapper<T>, T extends BaseEntity, E, C extends BasicConvert> implements CrudService<T, E> {

    public static Object db() {
        return null;
    }

    @Inject
    private C convert;

    @Inject
    private M dao;


    @Override
    public E update(E e) {
        if (e == null) {
            return null;
        }

        //T t = toSource(e);
        return update(e);
    }

    @Override
    public E update(T t) {
        if (t == null) {
            return null;
        }

        return null;
    }
}