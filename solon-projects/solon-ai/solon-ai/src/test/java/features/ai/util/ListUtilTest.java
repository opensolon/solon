package features.ai.util;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/2/28 created
 */
public class ListUtilTest {
    @Test
    public void case1() {
        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            docs.add(new Document("ddd" + i));
        }

        List<List<Document>> tmp = ListUtil.partition(docs, 20);

        assert tmp.size() == 1;
        assert tmp.get(0).size() == 1;
    }

    @Test
    public void case2() {
        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            docs.add(new Document("ddd" + i));
        }

        List<List<Document>> tmp = ListUtil.partition(docs, 20);

        assert tmp.size() == 1;
        assert tmp.get(0).size() == 20;
    }

    @Test
    public void case3() {
        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            docs.add(new Document("ddd" + i));
        }

        List<List<Document>> tmp = ListUtil.partition(docs, 20);

        assert tmp.size() == 2;
        assert tmp.get(0).size() == 20;
        assert tmp.get(1).size() == 1;
    }

    @Test
    public void case4() {
        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < 56; i++) {
            docs.add(new Document("ddd" + i));
        }

        List<List<Document>> tmp = ListUtil.partition(docs, 20);

        assert tmp.size() == 3;
        assert tmp.get(0).size() == 20;
        assert tmp.get(1).size() == 20;
        assert tmp.get(2).size() == 16;
    }
}
