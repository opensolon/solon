/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common;

import java.nio.ByteBuffer;

/**
 * 字节树(ByteTree)是一种高效的字节序列查找树数据结构，用于快速匹配和检索字节序列。
 * 它通过树形结构组织字节数据，支持快速查找、添加节点，并可以为节点绑定附加对象。
 * 主要应用于HTTP头部解析、URL路由匹配等需要高效字符串匹配的场景。
 * <p>
 * 特点：
 * 1. 支持动态添加节点，构建字节序列查找树
 * 2. 提供高效的字节序列查找功能
 * 3. 支持为节点绑定附加对象
 * 4. 具有容量管理机制，防止内存过度使用
 * 5. 支持虚拟节点，用于临时匹配结果
 *
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class ByteTree<T> {
    /**
     * 默认字节树实例，可用于通用场景
     */
    public final static ByteTree<?> DEFAULT = new ByteTree<>();

    /**
     * 空格字符结束匹配器，当遇到空格字符(ASCII 32)时返回true
     */
    public final static EndMatcher SP_END_MATCHER = endByte -> endByte == FeatUtils.SP;

    /**
     * 冒号结束匹配器，当遇到冒号字符(':')时返回true
     */
    public final static EndMatcher COLON_END_MATCHER = endByte -> endByte == ':';

    /**
     * 回车符结束匹配器，当遇到回车符(ASCII 13)时返回true
     */
    public final static EndMatcher CR_END_MATCHER = endByte -> endByte == FeatUtils.CR;

    /**
     * 字节树的最大深度限制，防止无限递归和栈溢出
     */
    public static final int MAX_DEPTH = 32;

    /**
     * 空结束匹配器，永远返回false，用于构建完整字节序列的节点
     */
    private static final EndMatcher NULL_END_MATCHER = endByte -> false;

    /**
     * 当前节点的字节值
     */
    private final byte value;

    /**
     * 当前节点在树中的深度，根节点深度为0
     */
    private final int depth;

    /**
     * 当前节点的父节点引用
     */
    private final ByteTree<T> parent;

    /**
     * 节点的字符串值缓存，避免重复计算
     */
    protected String stringValue;

    /**
     * 子节点数组的偏移量，用于优化数组空间
     * 初始值为-1表示尚未初始化
     */
    private int shift = -1;

    /**
     * 子节点数组，存储当前节点的所有子节点
     * 初始容量为1，会根据需要动态扩容
     */
    private ByteTree<T>[] nodes = new ByteTree[1];

    /**
     * 绑定到当前节点的附件对象
     */
    private T attach;

    /**
     * 创建一个指定容量限制的字节树
     * 该构造方法用于创建具有自定义容量限制的根节点
     */
    public ByteTree() {
        this(null, Byte.MIN_VALUE);
    }

    /**
     * 创建一个字节树节点
     * 该构造方法主要用于创建子节点
     *
     * @param parent 父节点，如果为null则创建根节点
     * @param value  节点的字节值
     * @throws IllegalStateException 如果节点深度超过最大深度限制(128)
     */
    ByteTree(ByteTree<T> parent, byte value) {
        this.parent = parent;
        this.value = value;
        // 计算节点深度：根节点深度为0，子节点深度为父节点深度+1
        this.depth = parent == null ? 0 : parent.depth + 1;
        // 检查深度是否超过最大限制
        if (depth > MAX_DEPTH) {
            throw new IllegalStateException("maxDepth is " + MAX_DEPTH + " , current is " + depth);
        }
    }

    /**
     * 在字节树中搜索匹配的字节序列
     * 该方法是ByteTree的核心功能，用于在字节缓冲区中查找匹配的字节序列
     *
     * @param buffer     要搜索的字节缓冲区
     * @param endMatcher 结束匹配器，用于确定何时结束搜索
     * @return 匹配到的字节树节点，如果未匹配到则返回null或虚拟节点
     */
    public ByteTree<T> search(ByteBuffer buffer, EndMatcher endMatcher) {
        // 是否处于前导空格修剪状态
        boolean trimState = true;
        // 标记初始位置，用于回溯
        int markPosition = buffer.position();
        // 从当前节点开始搜索
        ByteTree<T> byteTree = this;

        // 遍历字节缓冲区
        while (buffer.hasRemaining()) {
            byte v = buffer.get();
            // 跳过前导空格
            if (trimState) {
                if (v == FeatUtils.SP) {
                    continue;
                } else {
                    trimState = false;
                    // 记录第一个非空格字符的位置
                    markPosition = buffer.position() - 1;
                }
            }

            // 检查是否遇到结束字符
            if (endMatcher.match(v)) {
                return byteTree;
            }

            // 计算子节点索引
            int i = v - byteTree.shift;
            // 检查索引是否越界
            if (i < 0 || i >= byteTree.nodes.length) {
                break;
            }

            // 获取子节点
            byteTree = byteTree.nodes[i];
            if (byteTree == null) {
                // 未找到匹配的子节点，结束搜索
                break;
            }
        }

        // 缓冲区已读完，未匹配到，重置position
        if (!buffer.hasRemaining()) {
            buffer.position(markPosition);
            return null;
        }

        // 回退一个字节，准备构建虚拟节点
        buffer.position(buffer.position() - 1);
        // 构建临时虚拟节点，用完由JVM回收
        while (buffer.hasRemaining()) {
            if (endMatcher.match(buffer.get())) {
                // 找到结束字符，构建虚拟节点
                int length = buffer.position() - markPosition;
                byte[] data = new byte[length];
                buffer.position(buffer.position() - length);
                buffer.get(data, 0, length);
                // 支持中文解析，https://gitee.com/smartboot/feat/issues/IJPD9F
                return new VirtualByteTree(new String(data, 0, length - 1));
            }
        }
        buffer.position(markPosition);
        return null;
    }

    /**
     * 添加一个带附件的节点
     * 该方法将字符串值转换为字节数组，并从根节点开始添加到字节树中
     *
     * @param value  节点的字符串值
     * @param attach 要绑定到节点的附件对象
     */
    public void addNode(String value, T attach) {
        byte[] bytes = value.getBytes();
        // 获取根节点，无论当前节点在树的哪个位置，都从根节点开始添加
        ByteTree<T> tree = this;
        while (tree.depth > 0) {
            tree = tree.parent;
        }
        // 添加节点并设置附件
        ByteTree<T> leafNode = tree.addNode(bytes, 0, bytes.length, NULL_END_MATCHER);
        leafNode.stringValue = value;
        leafNode.attach = attach;
    }

    /**
     * 从根节点开始，为入参字符串创建节点
     * 这是addNode(String value, T attach)方法的简化版本，不绑定附件对象
     *
     * @param value 要添加的节点的字符串值
     */
    public void addNode(String value) {
        addNode(value, null);
    }

    /**
     * 递归添加节点
     * 该方法是字节树节点添加的核心实现，通过递归方式逐字节构建树结构
     *
     * @param value      要添加的字节数组
     * @param offset     当前处理的字节偏移量
     * @param limit      字节数组的长度限制
     * @param endMatcher 结束匹配器，用于确定何时停止添加
     * @return 添加的叶子节点，即字节序列的最后一个节点
     */
    private synchronized ByteTree<T> addNode(byte[] value, int offset, int limit, EndMatcher endMatcher) {
        // 已处理完所有字节或达到最大深度，返回当前节点
        if (offset == limit || this.depth >= MAX_DEPTH) {
            return this;
        }

        // 获取当前要处理的字节
        byte b = value[offset];
        // 如果遇到结束字符，返回当前节点
        if (endMatcher.match(b)) {
            return this;
        }

        // 初始化shift值，用于优化子节点数组的空间
        // 首次添加子节点时，将shift设置为该字节的值
        if (shift == -1) {
            shift = b;
        }

        // 确保子节点数组有足够空间
        // 如果当前字节小于shift，需要扩展数组并调整shift
        if (b - shift < 0) {
            increase(b - shift);
        }
        // 如果当前字节大于等于shift+数组长度，需要扩展数组
        else {
            increase(b + 1 - shift);
        }

        // 获取或创建下一级节点
        ByteTree<T> nextTree = nodes[b - shift];
        if (nextTree == null) {
            // 创建新节点并更新计数器
            nextTree = nodes[b - shift] = new ByteTree<T>(this, b);
        }

        // 递归处理下一个字节
        return nextTree.addNode(value, offset + 1, limit, endMatcher);
    }

    /**
     * 调整子节点数组的大小
     * 该方法根据需要扩展或重新分配子节点数组，以适应新的字节值
     *
     * @param size 需要的数组大小或调整量
     */
    private void increase(int size) {
        // 特殊情况处理：如果size为0，将其设为-1
        if (size == 0) size = -1;

        // 处理负值情况：需要在数组前面添加空间
        if (size < 0) {
            // 创建更大的数组
            ByteTree<T>[] temp = new ByteTree[nodes.length - size];
            // 将原数组内容复制到新数组，偏移-size个位置
            System.arraycopy(nodes, 0, temp, -size, nodes.length);
            // 更新节点数组引用
            nodes = temp;
            // 调整shift值，使索引计算保持正确
            shift += size;
        }
        // 处理正值情况：需要扩展数组大小
        else if (nodes.length < size) {
            // 创建更大的数组
            ByteTree<T>[] temp = new ByteTree[size];
            // 将原数组内容复制到新数组
            System.arraycopy(nodes, 0, temp, 0, nodes.length);
            // 更新节点数组引用
            nodes = temp;
        }
    }

    /**
     * 获取节点的字符串值
     * 该方法返回当前节点表示的字符串，如果字符串值未缓存，则从节点路径构建
     * 字符串值是从根节点到当前节点的路径上所有字节值组成的字符串
     *
     * @return 节点的字符串值，表示从根节点到当前节点的完整路径
     */
    public String getStringValue() {
        if (stringValue == null) {
            // 如果字符串值未缓存，则从节点路径构建字符串
            byte[] b = new byte[depth];
            ByteTree<T> tree = this;
            // 从当前节点向上遍历到根节点，收集路径上的所有字节值
            while (tree.depth != 0) {
                // 注意这里是倒序存储，因为我们是从叶子节点向根节点遍历
                b[tree.depth - 1] = tree.value;
                tree = tree.parent;
            }
            // 将字节数组转换为字符串并缓存
            stringValue = new String(b);
        }
        return stringValue;
    }

    /**
     * 获取节点绑定的附件对象
     * 附件对象是在调用addNode(String value, T attach)方法时绑定到节点的泛型对象
     * 可以用于存储与该节点相关的任何类型的数据或功能对象
     *
     * @return 绑定到当前节点的附件对象，如果没有绑定则返回null
     */
    public T getAttach() {
        return attach;
    }

    /**
     * 结束匹配器接口，用于确定何时结束字节序列的处理
     * 这是一个函数式接口，用于在搜索和添加节点过程中判断何时停止处理
     * ByteTree预定义了几个常用的结束匹配器：SP_END_MATCHER、COLON_END_MATCHER和CR_END_MATCHER
     */
    public interface EndMatcher {
        /**
         * 判断当前字节是否为结束字符
         * 当遇到满足条件的字节时，搜索或添加操作将停止并返回当前节点
         *
         * @param endByte 要检查的字节值
         * @return 如果是结束字符则返回true，否则返回false
         */
        boolean match(byte endByte);
    }

    /**
     * 虚拟字节树节点，用于临时存储字符串值
     * 这是一个特殊的节点类型，不会添加到实际的树结构中
     * 主要用于search方法中，当在树中未找到匹配但需要返回结果时创建
     * 由于不加入树结构，使用后会被JVM垃圾回收
     */
    private class VirtualByteTree extends ByteTree<T> {

        /**
         * 创建一个虚拟字节树节点
         * 该构造方法直接设置节点的字符串值，不参与树的构建过程
         *
         * @param value 节点的字符串值，通常是从ByteBuffer中提取的字符串
         */
        public VirtualByteTree(String value) {
            super(); // 调用父类构造方法创建根节点
            this.stringValue = value; // 直接设置字符串值，跳过正常的节点构建过程
        }
    }
}
