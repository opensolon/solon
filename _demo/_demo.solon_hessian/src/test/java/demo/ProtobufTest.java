package demo;

import io.edap.x.protobuf.ProtoBuf;
import org.junit.Test;
import server.model.ComplexModel;
import server.model.Person;
import server.model.Point;

import java.util.*;

public class ProtobufTest {
    @Test
    public void test1() throws Exception {
        String hello = "你好";

        byte[] tmp = ProtoBuf.ser(hello);

        assert ProtoBuf.der(tmp).equals("你好");
    }

    @Test
    public void test2() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "niko");

        byte[] tmp = ProtoBuf.ser(map);

        assert ((Map) ProtoBuf.der(tmp)).size() == 1;
    }

    @Test
    public void test3() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "niko");

        byte[] tmp = ProtoBuf.ser(map);

        assert ((Map) ProtoBuf.der(tmp)).size() == 1;
    }

    @Test
    public void test4() throws Exception {
        ComplexModel<Point> model = new ComplexModel<Point>();
        model.setId(1);
        Person person = new Person();
        person.setName("Tom");
        person.setAge(86);
        person.setBirthDay(new Date());
        person.setSensitiveInformation("This should be private over the wire");
        model.setPerson(person);

        List<Point> points = new ArrayList<Point>();
        Point point = new Point();
        point.setX(3);
        point.setY(4);
        points.add(point);

        point = new Point();
        point.setX(100);
        point.setY(100);
        points.add(point);

        //远程方法调用
        model.setPoints(points);


        byte[] tmp = ProtoBuf.ser(model);

        assert ((ComplexModel<Point>) ProtoBuf.der(tmp)).getPoints().size() > 1;
    }

    @Test
    public void test5() throws Exception {
        ComplexModel<Point> model = new ComplexModel<Point>();
        model.setId(1);
        Person person = new Person();
        person.setName("Tom");
        person.setAge(86);
        person.setBirthDay(new Date());
        person.setSensitiveInformation("This should be private over the wire");
        model.setPerson(person);

        List<Point> points = new ArrayList<Point>();
        Point point = new Point();
        point.setX(3);
        point.setY(4);
        points.add(point);

        point = new Point();
        point.setX(100);
        point.setY(100);
        points.add(point);

        //远程方法调用
        model.setPoints(points);

        Map<String, Object> map = new HashMap<>();
        map.put("model", model);


        byte[] tmp = ProtoBuf.ser(map);

        Map<String, Object> rst = (Map<String, Object>) ProtoBuf.der(tmp);

        assert rst.size() == 1;
        assert ((ComplexModel<Point>) rst.get("model")).getPoints().size() > 1;
    }
}
