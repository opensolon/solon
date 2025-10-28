package features.solon.generic1;

// 示例类，用于测试 TypeVariable 边界 (T extends Number & Comparable<T>)
class SimpleGeneric<T extends Number & Comparable<T>> {}