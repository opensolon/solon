package features.rx;

import org.junit.jupiter.api.Test;
import org.noear.solon.rx.Completable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/4/18 created
 */
public class CompletableTest {
    @Test
    public void testThen() {
        List<String> list = new ArrayList<>();

        Completable.create(emitter -> {
            list.add("a");
            emitter.onComplete();
        }).then(Completable.create((emitter1) -> {
            list.add("b");
            emitter1.onComplete();
        })).subscribe();

        System.out.println(list);

        assert list.size() == 2;
        assert "a".equals(list.get(0));
        assert "b".equals(list.get(1));
    }
}
