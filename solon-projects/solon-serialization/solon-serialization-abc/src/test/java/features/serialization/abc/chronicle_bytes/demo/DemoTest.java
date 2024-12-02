package features.serialization.abc.chronicle_bytes.demo;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.solon.ChrBytesSerializable;
import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.abc.AbcBytesSerializer;

import java.io.IOException;

public class DemoTest {
    @Test
    public void case1() throws IOException {
        PersonDo personDo = new PersonDo();
        personDo.name = "张三丰";
        personDo.age = 102;
        personDo.height = 1.75;

        AbcBytesSerializer serializer = new AbcBytesSerializer();
        byte[] data = serializer.serialize(personDo);
        System.out.println(data.length);

        PersonDo personDo2 = (PersonDo) serializer.deserialize(data, PersonDo.class);
        System.out.println("name:" + personDo2.name + ",age:" + personDo2.age + ",height:" + personDo2.height);

        assert personDo.name.equals(personDo2.name);
        assert personDo.age == personDo2.age;
        assert personDo.height == personDo2.height;
    }

    public static class PersonDo implements ChrBytesSerializable {
        public String name;
        public int age;
        public double height;

        @Override
        public void serializeRead(BytesIn in) {
            this.name = in.readUtf8();
            this.age = in.readInt();
            this.height = in.readDouble();
        }

        @Override
        public void serializeWrite(BytesOut out) {
            out.writeUtf8(this.name);
            out.writeInt(this.age);
            out.writeDouble(this.height);
        }
    }

}