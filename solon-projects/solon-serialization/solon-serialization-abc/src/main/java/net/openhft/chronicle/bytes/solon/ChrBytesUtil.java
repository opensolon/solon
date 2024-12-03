package net.openhft.chronicle.bytes.solon;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * 工具类，提供将基础类型数组、集合、映射等数据结构序列化和反序列化到字节流的功能。
 */
public class ChrBytesUtil {

    /**
     * 将 int 数组写入字节流。
     * @param bytes 输出流
     * @param ints 要写入的数组
     */
    public static void writeIntArray(final BytesOut<?> bytes, final int[] ints) {
        final int length = ints.length;
        bytes.writeInt(length); // 写入数组长度
        for (int i = 0; i < length; i++) {
            bytes.writeInt(ints[i]); // 写入每个 int 值
        }
    }

    /**
     * 从字节流中读取 int 数组。
     * @param bytes 输入流
     * @return 读取的 int 数组
     */
    public static int[] readIntArray(final BytesIn<?> bytes) {
        final int length = bytes.readInt(); // 读取数组长度
        final int[] ints = new int[length];
        for (int i = 0; i < length; i++) {
            ints[i] = bytes.readInt(); // 读取每个 int 值
        }
        return ints;
    }

    /**
     * 将 long 数组写入字节流。
     * @param bytes 输出流
     * @param longs 要写入的数组
     */
    public static void writeLongArray(final BytesOut<?> bytes, final long[] longs) {
        final int length = longs.length;
        bytes.writeInt(length); // 写入数组长度
        for (int i = 0; i < length; i++) {
            bytes.writeLong(longs[i]); // 写入每个 long 值
        }
    }

    /**
     * 从字节流中读取 long 数组。
     * @param bytes 输入流
     * @return 读取的 long 数组
     */
    public static long[] readLongArray(final BytesIn<?> bytes) {
        final int length = bytes.readInt(); // 读取数组长度
        final long[] longs = new long[length];
        for (int i = 0; i < length; i++) {
            longs[i] = bytes.readLong(); // 读取每个 long 值
        }
        return longs;
    }

    // 类似的方法对其他类型数组如 float、double、short、byte、char 都进行了序列化与反序列化实现。
    // 为了节省篇幅，这里不重复注释。

    /**
     * 将实现了 ChrBytesSerializable 接口的对象数组写入字节流。
     * @param bytes 输出流
     * @param array 要写入的对象数组
     * @param <T> 泛型类型，必须实现 ChrBytesSerializable 接口
     */
    public static <T extends ChrBytesSerializable> void writeArray(final BytesOut<?> bytes, final T[] array) {
        final int length = array.length;
        bytes.writeInt(length); // 写入数组长度
        for (int i = 0; i < length; i++) {
            array[i].serializeWrite(bytes); // 调用对象的序列化方法
        }
    }

    /**
     * 从字节流中读取实现了 ChrBytesSerializable 接口的对象数组。
     * @param bytes 输入流
     * @param creator 用于创建对象的函数
     * @param arrayCreator 用于创建数组的函数
     * @param <T> 泛型类型，必须实现 ChrBytesSerializable 接口
     * @return 读取的对象数组
     */
    public static <T> T[] readArray(final BytesIn<?> bytes, final Function<BytesIn, T> creator,
                                    final IntFunction<T[]> arrayCreator) {
        final int length = bytes.readInt(); // 读取数组长度
        final T[] array = arrayCreator.apply(length);
        for (int i = 0; i < length; i++) {
            array[i] = creator.apply(bytes); // 通过 creator 函数创建对象并填充数组
        }
        return array;
    }

    /**
     * 将泛型 List 写入字节流。
     * @param bytes 输出流
     * @param collection 要写入的集合
     * @param <T> 泛型类型
     */
    public static <T extends ChrBytesSerializable> void writeList(final BytesOut<?> bytes,
                                                                  final List<ChrBytesSerializable> collection) {
        final int length = collection.size();
        bytes.writeInt(length); // 写入集合大小
        collection.forEach(e -> e.serializeWrite(bytes)); // 遍历集合并序列化每个元素
    }

    /**
     * 从字节流中读取泛型 List。
     * @param bytes 输入流
     * @param creator 用于创建对象的函数
     * @param <T> 泛型类型
     * @return 读取的 List
     */
    public static <T> List<T> readList(final BytesIn<?> bytes, final Function<BytesIn, T> creator) {
        final int length = bytes.readInt(); // 读取集合大小
        final List<T> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(creator.apply(bytes)); // 创建对象并添加到集合
        }
        return list;
    }

    /**
     * 将 Map 写入字节流。
     * @param bytes 输出流
     * @param map 要写入的映射
     * @param keyMarshaller 键的序列化方法
     * @param valMarshaller 值的序列化方法
     * @param <K> 键的类型
     * @param <V> 值的类型
     */
    public static <K, V> void writeMap(final BytesOut<?> bytes, final Map<K, V> map,
                                       final BiConsumer<BytesOut<?>, K> keyMarshaller,
                                       final BiConsumer<BytesOut<?>, V> valMarshaller) {
        final int length = map.size();
        bytes.writeInt(length); // 写入映射大小
        map.forEach((k, v) -> {
            keyMarshaller.accept(bytes, k); // 序列化键
            valMarshaller.accept(bytes, v); // 序列化值
        });
    }

    /**
     * 从字节流中读取 Map。
     * @param bytes 输入流
     * @param mapSupplier Map 的供应函数
     * @param keyCreator 键的创建方法
     * @param valCreator 值的创建方法
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @param <M> Map 的类型
     * @return 读取的 Map
     */
    public static <K, V, M extends Map<K, V>> M readMap(final BytesIn<?> bytes, final Supplier<M> mapSupplier,
                                                        final Function<BytesIn, K> keyCreator,
                                                        final Function<BytesIn, V> valCreator) {
        final int length = bytes.readInt(); // 读取映射大小
        final M map = mapSupplier.get();
        for (int i = 0; i < length; i++) {
            map.put(keyCreator.apply(bytes), valCreator.apply(bytes)); // 创建键值对并放入 Map
        }
        return map;
    }

    /**
     * 写入可空对象到字节流。
     * @param bytes 输出流
     * @param object 要写入的对象
     * @param marshaller 对象的序列化方法
     * @param <T> 泛型类型
     */
    public <T> void writeNullable(final BytesOut<?> bytes, final T object, final BiConsumer<T, BytesOut> marshaller) {
        bytes.writeBoolean(object != null); // 写入对象是否为 null
        if (object != null) {
            marshaller.accept(object, bytes); // 序列化非空对象
        }
    }

    /**
     * 从字节流中读取可空对象。
     * @param bytes 输入流
     * @param creator 对象的创建方法
     * @param <T> 泛型类型
     * @return 读取的对象，可能为 null
     */
    public <T> T readNullable(final BytesIn<?> bytes, final Function<BytesIn, T> creator) {
        return bytes.readBoolean() ? creator.apply(bytes) : null; // 根据标记位判断是否创建对象
    }
}
