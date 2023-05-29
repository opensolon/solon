package test2;

/**
 * @author noear 2022/9/30 created
 */
public interface CrudService<T extends BaseEntity, E> {

    E update(T t);

    E update(E e);

}