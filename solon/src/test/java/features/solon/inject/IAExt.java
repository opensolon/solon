package features.solon.inject;

/**
 * @author noear 2024/12/17 created
 */
public interface IAExt extends IA {
    default void b() {
        System.out.println("b-def");
    }
}
