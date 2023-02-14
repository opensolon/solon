package features;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author noear 2023/2/14 created
 */
public class Mock2Test {
    @Test
    public void test(){
        List<String> list = mock(List.class);
        list.add("1");
        list.add("2");

        System.out.println(list.get(0)); // 会得到null ，前面只是在记录行为而已，没有往list中添加数据

        verify(list).add("1"); // 正确，因为该行为被记住了
        assert true;

        try {
            verify(list).add("3");//报错，因为前面没有记录这个行为
            assert false;
        }catch (Throwable e){
            assert true;
        }
    }
}
