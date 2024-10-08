package labs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2024/10/8 created
 */
public class IndexTest {
    public static void main(String[] args) {
        Map<String,Integer> map = case1();
        List<String> ary = case2();

        case1Test(map, 1000);
        case2Test(ary, 1000);

        System.out.println("-------------------");

        case1Test(map, 10000);
        case2Test(ary, 10000);

        System.out.println("-------------------");

        case1Test(map, 1000000);
        case2Test(ary, 1000000);
    }

    private static void case1Test(Map<String,Integer> map, int count) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            map.get("a");
            map.get("z");
        }

        System.out.println("case1: " + (System.currentTimeMillis() - start));
    }

    private static Map<String,Integer> case1(){
        Map<String,Integer> map = new HashMap<>();
        map.put("a",1);
        map.put("b",2);
        map.put("c",3);
        map.put("d",4);
        map.put("e",5);
        map.put("f",6);
        map.put("g",7);
        map.put("h",8);
        map.put("i",9);
        map.put("j",10);
        map.put("k",11);
        map.put("l",12);
        map.put("m",13);
        map.put("n",14);
        map.put("o",15);
        map.put("p",16);
        map.put("q",17);
        map.put("r",18);
        map.put("s",19);
        map.put("t",20);
        map.put("u",21);
        map.put("v",22);
        map.put("w",23);
        map.put("x",24);
        map.put("y",25);
        map.put("z",26);
        map.put("A",27);
        map.put("B",28);
        map.put("C",29);
        map.put("D",30);
        map.put("E",31);
        map.put("F",32);

        return map;
    }

    private static void case2Test(List<String> ary, int count) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            ary.indexOf("a");
            ary.indexOf("z");
        }

        System.out.println("case2: " + (System.currentTimeMillis() - start));
    }

    private static List<String> case2(){
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        list.add("f");
        list.add("g");
        list.add("h");
        list.add("i");
        list.add("j");
        list.add("k");
        list.add("l");
        list.add("m");
        list.add("n");
        list.add("o");
        list.add("p");
        list.add("q");
        list.add("r");
        list.add("s");
        list.add("t");
        list.add("u");
        list.add("v");
        list.add("w");
        list.add("x");
        list.add("y");
        list.add("z");
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D");
        list.add("E");
        list.add("F");

        return list;
    }
}
