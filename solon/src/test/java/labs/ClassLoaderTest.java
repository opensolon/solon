package labs;

/**
 * @author noear 2024/6/27 created
 */
public class ClassLoaderTest {
    public static void main(String[] args) {
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader);
        System.out.println(ClassLoader.getSystemClassLoader());

        assert classLoader == ClassLoader.getSystemClassLoader();
    }
}
