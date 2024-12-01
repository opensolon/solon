package features.genetic;

import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.abc.AbcBytesSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2024/10/24 created
 */
public class GeneticTest {
    @Test
    public void case1() throws Exception {
        List<Book> dataRaw = new ArrayList<>();
        Book book = new Book();
        book.setTitle("a");
        dataRaw.add(book);

        ///////////////////////

        AbcBytesSerializer serializer = new AbcBytesSerializer();
        byte[] data = serializer.serialize(dataRaw);
        System.out.println(data);


        List<Book> list0 = (List<Book>) serializer.deserialize(data, new ArrayList<Book>() {
        }.getClass());

        assert list0 != null;
        assert list0.size() == 1;
        assert "a".equals(list0.get(0).title);


        List list1 = (List) serializer.deserialize(data, new ArrayList() {
        }.getClass());

        assert list1 != null;
        assert list1.size() == 1;
    }

    static class Book {
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
