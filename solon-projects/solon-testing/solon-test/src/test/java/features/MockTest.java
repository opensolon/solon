/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package features;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author noear 2023/2/14 created
 */
public class MockTest {
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
