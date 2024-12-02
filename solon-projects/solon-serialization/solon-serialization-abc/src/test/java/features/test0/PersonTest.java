package features.test0;

import features.model.PersonDo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.abc.AbcBytesSerializer;

public class PersonTest {
    @SneakyThrows
    @Test
    public void case1() {
        PersonDo personDo = new PersonDo();
        personDo.name = "张三丰";
        personDo.age = 102;
        personDo.height = 1.75;

        AbcBytesSerializer serializer = new AbcBytesSerializer();
        byte[] data = serializer.serialize(personDo);
        System.out.println(data.length);

        PersonDo personDo2 = (PersonDo) serializer.deserialize(data, PersonDo.class);
        System.out.println("name:"+personDo2.name +",age:"+personDo2.age+",height:"+personDo2.height);

        assert personDo.name.equals(personDo2.name);
        assert personDo.age == personDo2.age;
        assert personDo.height == personDo2.height;


    }
}