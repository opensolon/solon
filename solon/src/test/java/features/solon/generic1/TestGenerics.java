package features.solon.generic1;

import com.sun.tools.javac.util.List;

// 辅助类：用于获取各种复杂的 Type 类型
class TestGenerics<T extends Number & Comparable<T>, E> {
    // 内部类，用于测试 OwnerType
    class InnerClass<S> {}

    // 字段，用于获取 Type 实例
    List<String> pTypeField; // ParameterizedType
    T typeVarField; // TypeVariable
    T[] gArrayField; // GenericArrayType (TypeVariable component)
    String[] classArrayField; // Class Array (Class component)
    List<?> wildcardUnboundField; // WildcardType (Unbound: ?)
    List<? extends Number> wildcardExtendsField; // WildcardType (Upper Bound: Number)
    List<? super Integer> wildcardSuperField; // WildcardType (Lower Bound: Integer)
    TestGenerics<T, E>.InnerClass<String> innerTypeField; // ParameterizedType with OwnerType
}