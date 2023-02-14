package features;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author noear 2023/2/14 created
 */
public class Mock2Test {
    @Test
    public void testBehavior() {
        List<String> list = mock(List.class);
        list.add("1");
        list.add("2");

        System.out.println(list.get(0)); // 会得到null ，前面只是在记录行为而已，没有往list中添加数据

        assertFalse(verify(list).add("1")); // 正确，因为该行为被记住了

        assertThrows(Error.class, () -> {
            verify(list).add("3");//报错，因为前面没有记录这个行为
        });
    }

    @Test
    void testStub() {
        List<Integer> l = mock(ArrayList.class);

        when(l.get(0)).thenReturn(10);
        when(l.get(1)).thenReturn(20);
        when(l.get(2)).thenThrow(new RuntimeException("no such element"));

        assertEquals(l.get(0), 10);
        assertEquals(l.get(1), 20);
        assertNull(l.get(4));
        assertThrows(RuntimeException.class, () -> {
            int x = l.get(2);
        });
    }

    @Test
    void testVoidStub() {
        List<Integer> l = mock(ArrayList.class);
        doReturn(10).when(l).get(1);
        doThrow(new RuntimeException("you cant clear this List")).when(l).clear();

        assertEquals(l.get(1), 10);
        assertThrows(RuntimeException.class, () -> l.clear());
    }

    @Test
    void testMatchers() {
        List<Integer> l = mock(ArrayList.class);
        when(l.get(anyInt())).thenReturn(100);

        assertEquals(l.get(999), 100);
    }
}
