package demo;

import io.edap.x.protobuf.ProtoBuf;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProtobufTest {
    @Test
    public void test1() throws Exception {
        String hello = "你好";

        byte[] tmp = ProtoBuf.ser(hello);

        assert ProtoBuf.der(tmp).equals("你好");
    }

    @Test
    public void test2() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("name","niko");

        byte[] tmp = ProtoBuf.ser(map);

        assert ((Map)ProtoBuf.der(tmp)).size() == 1;
    }

    @Test
    public void test3() throws Exception {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("name","niko");

        byte[] tmp = ProtoBuf.ser(map);

        assert ((Map)ProtoBuf.der(tmp)).size() == 1;
    }
}
