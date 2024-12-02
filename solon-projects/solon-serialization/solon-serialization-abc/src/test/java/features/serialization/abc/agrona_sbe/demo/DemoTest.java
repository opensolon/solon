package features.serialization.abc.agrona_sbe.demo;

import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.abc.AbcBytesSerializer;
import org.agrona.sbe.solon.SbeInput;
import org.agrona.sbe.solon.SbeOutput;
import org.agrona.sbe.solon.SbeSerializable;

/**
 * @author noear 2024/10/24 created
 */
public class DemoTest {
    @Test
    public void case1() throws Exception {
        Book book = new Book();
        book.setTitle("a");

        ///////////////////////

        AbcBytesSerializer serializer = new AbcBytesSerializer();
        byte[] data = serializer.serialize(book);
        System.out.println(data);

        Book book1 = (Book) serializer.deserialize(data, Book.class);

        System.out.println(book1);
        assert book.getTitle().equals(book1.getTitle());
    }

    public static class Book implements SbeSerializable {
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public void serializeRead(SbeInput in) {
            title = in.readString();
        }

        @Override
        public void serializeWrite(SbeOutput out) {
            out.writeString(title);
        }

        @Override
        public String toString() {
            return "Book{" +
                    "title='" + title + '\'' +
                    '}';
        }
    }
}
