package features.solon.generic1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.noear.solon.core.util.GenericUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author noear 2025/10/28 created
 *
 */
public class GenericUtilMatchedTest {
    // --- Reflection Helpers ---

    private Type getTypeFromField(String fieldName) throws Exception {
        return TestGenerics.class.getDeclaredField(fieldName).getGenericType();
    }

    private Type getTypeFromMethod(Class<?> targetClass, String methodName) throws Exception {
        return targetClass.getDeclaredMethod(methodName).getGenericReturnType();
    }

    // 获取一个 TypeVariable 实例
    private Type getTypeVariable(String fieldName) throws Exception {
        return ((ParameterizedType) getTypeFromField(fieldName)).getActualTypeArguments()[0];
    }

    // 获取一个 Method TypeVariable (S)
    private Type getMethodTypeVariable() throws Exception {
        class Dummy {
            public <S> List<S> getList() {
                return null;
            }
        }
        return Dummy.class.getDeclaredMethod("getList").getTypeParameters()[0];
    }

    // 获取一个 Method GenericArrayType (S[])
    private Type getMethodGenericArrayType() throws Exception {
        class Dummy {
            public <S> S[] getArray() {
                return null;
            }
        }
        return Dummy.class.getDeclaredMethod("getArray").getGenericReturnType();
    }

    // 获取一个 ParameterizedType
    private Type getParameterizedType(Class<?> rawType, Type... args) {
        return new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getRawType() {
                return rawType;
            }

            public Type getOwnerType() {
                return null;
            }

            public String toString() {
                return rawType.getTypeName() + "<" + Arrays.toString(args) + ">";
            }
        };
    }

    // 获取一个 TypeVariable (T extends Number)
    private Type getTestTypeVariable() throws Exception {
        return getTypeFromField("typeVarField");
    }

    // 获取一个 Unbound Wildcard (?)
    private WildcardType getUnboundWildcard() throws Exception {
        return (WildcardType) ((ParameterizedType) getTypeFromField("wildcardUnboundField")).getActualTypeArguments()[0];
    }

    // 获取一个 extends Wildcard (? extends Number)
    private WildcardType getExtendsWildcard() throws Exception {
        return (WildcardType) ((ParameterizedType) getTypeFromField("wildcardExtendsField")).getActualTypeArguments()[0];
    }

    // 获取一个 super Wildcard (? super Integer)
    private WildcardType getSuperWildcard() throws Exception {
        return (WildcardType) ((ParameterizedType) getTypeFromField("wildcardSuperField")).getActualTypeArguments()[0];
    }

    // --- Argument Source for Parameterized Tests ---

    // 测试基础类型匹配（typeMatched 的快速路径和 dispatchTypeMatch 的返回 false 路径）
    private static Stream<Arguments> baseMatchCases() {
        return Stream.of(
                // 1. Trivial Match (Class)
                Arguments.of(String.class, String.class, true, "String vs String"),
                // 2. Trivial Mismatch (Class)
                Arguments.of(String.class, Integer.class, false, "String vs Integer"),
                // 3. Null Checks (Coverage)
                Arguments.of(null, String.class, false, "Null vs String"),
                Arguments.of(String.class, null, false, "String vs Null"),
                Arguments.of(null, null, true, "Null vs Null (typeMatched fast path)"),
                // 4. Raw vs Raw (Equals check)
                Arguments.of(List.class, List.class, true, "List vs List"),
                // 5. Raw vs PType (Mismatch - dispatchTypeMatch return false)
                Arguments.of(List.class, new ParameterizedTypeImpl(List.class, String.class), false, "List vs List<String>")
        );
    }

    // 简化的 ParameterizedType 实现
    private static class ParameterizedTypeImpl implements ParameterizedType {
        private final Type rawType;
        private final Type[] actualTypeArguments;

        public ParameterizedTypeImpl(Type rawType, Type... actualTypeArguments) {
            this.rawType = rawType;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ParameterizedType)) return false;
            ParameterizedType that = (ParameterizedType) obj;
            return this.rawType.equals(that.getRawType()) && Arrays.equals(this.actualTypeArguments, that.getActualTypeArguments());
        }
    }


    // --- Test Cases ---

    @ParameterizedTest(name = "{2} {3}")
    @MethodSource("baseMatchCases")
    @DisplayName("1. Base & Fast Path Tests")
    void testBaseAndFastPath(Type check, Type source, boolean expected, String name) {
        if (expected) {
            assertTrue(GenericUtil.typeMatched(check, source), name + " should match");
        } else {
            assertFalse(GenericUtil.typeMatched(check, source), name + " should not match");
        }
    }

    @Test
    @DisplayName("2. ParameterizedType Matching (matchParameterizedType)")
    void testParameterizedTypeMatching() throws Exception {
        Type listString = getTypeFromField("pTypeField"); // List<String>
        Type listInteger = getParameterizedType(List.class, Integer.class); // List<Integer>
        Type setString = getParameterizedType(Set.class, String.class); // Set<String>

        // PType vs PType (Match)
        assertTrue(GenericUtil.typeMatched(listString, listString), "PType vs PType (Self) should match");

        // PType vs PType (Argument Mismatch) -> Covers !typeMatched(arg)
        assertFalse(GenericUtil.typeMatched(listString, listInteger), "List<String> vs List<Integer> should not match");

        // PType vs PType (Raw Mismatch) -> Covers !typeMatched(rawType)
        assertFalse(GenericUtil.typeMatched(listString, setString), "List<String> vs Set<String> should not match");

        // PType vs Class (Mismatch) -> Covers !(sourceType instanceof PType)
        assertFalse(GenericUtil.typeMatched(listString, List.class), "List<String> vs List.class should not match");

        // PType vs PType (Argument Count Mismatch) -> Covers checkArgs.length != sourceArgs.length
        Type mapString = getParameterizedType(java.util.Map.class, String.class); // Map<String> (missing second arg)
        Type mapStringString = getParameterizedType(java.util.Map.class, String.class, String.class);
        assertFalse(GenericUtil.typeMatched(mapStringString, mapString), "Map<S,S> vs Map<S> should not match");
    }

    @Test
    @DisplayName("3. OwnerType Matching (matchOwnerType)")
    void testOwnerTypeMatching() throws Exception {
        Type innerType = getTypeFromField("innerTypeField"); // TestGenerics<T,E>.InnerClass<String>

        // 1. Owner Match (Self-match, handled by equals, but covers OwnerType logic implicitly)
        assertTrue(GenericUtil.typeMatched(innerType, innerType), "OwnerType self-match should work");

        // 2. Owner Mismatch (Different Owner Class) -> Covers !matchOwnerType
        class DifferentOuter {
            class InnerClass<S> {
            }
        }
        Type differentOwner = new ParameterizedTypeImpl(
                DifferentOuter.InnerClass.class, String.class, DifferentOuter.class
        );
        assertFalse(GenericUtil.typeMatched(innerType, differentOwner), "Different outer class should not match");

        // 3. OwnerType Null (Covers owner1 != null && owner2 != null)
        Type listString = getTypeFromField("pTypeField"); // Owner is null
        Type listStringWithFakeOwner = getParameterizedType(List.class, String.class);
        Type listStringNoOwner = new ParameterizedTypeImpl(List.class, String.class);

        // Faking a mismatch where owner is null but type has no owner (PTypeImpl uses null)
        assertTrue(GenericUtil.typeMatched(listString, listStringNoOwner), "Two PTypes with null owner should match");

        // 4. Owner Type Recursion (Not strictly necessary for 100% line coverage, but good practice)
        Type p1 = getParameterizedType(List.class, listString); // List<List<String>>
        Type p2 = getParameterizedType(List.class, listString);
        assertTrue(GenericUtil.typeMatched(p1, p2), "Recursive owner match (on args) should work");
    }

    @Test
    @DisplayName("4. GenericArrayType Matching (matchGenericArrayType)")
    void testGenericArrayTypeMatching() throws Exception {
        Type tArray = getTypeFromField("gArrayField"); // T[] (GAT with TypeVariable component)
        Type sArray = getMethodGenericArrayType(); // S[] (GAT with Method TypeVariable component)
        Type stringArray = getTypeFromField("classArrayField"); // String[] (Class Array)

        // 1. GAT vs GAT (Component Mismatch) -> Covers GAT vs GAT path
        assertFalse(GenericUtil.typeMatched(tArray, sArray), "T[] vs S[] should not match (different TypeVariables)");

        // 2. GAT vs Class[] (Mismatch: T vs String) -> Covers matchGenericArrayToClass (fails isAssignableFrom)
        assertFalse(GenericUtil.typeMatched(tArray, stringArray), "T[] (bounds to Number) vs String[] should not match");

        // 3. GAT vs Class[] (Match: T vs Object) -> Covers matchGenericArrayToClass (passes isAssignableFrom)
        class ArrayOfObject { public Object[] objArray; }
        Type objArrayClass = ArrayOfObject.class.getDeclaredField("objArray").getType(); // Object[].class

        assertTrue(GenericUtil.typeMatched(tArray, objArrayClass), "T[] vs Object[] should match (as T's bound is assignable)");

        // 4. GAT vs Class (Non-Array) -> Covers !classArray.isArray()
        assertFalse(GenericUtil.typeMatched(tArray, String.class), "GAT vs non-array Class should not match");

        // 5. GAT vs Other Type (Mismatch)
        assertFalse(GenericUtil.typeMatched(tArray, String.class), "GAT vs String should not match");

        // 6. 【新增】GAT vs Class[] (Component raw class is null) -> Covers genericComponentClass == null
        GenericArrayType gatOfUnknown = new GenericArrayType() {
            @Override
            public Type getGenericComponentType() {
                return new Type() {}; // Unknown type, extractRawClass returns null
            }
        };
        // 此时 genericComponentClass == null，应该返回 false
        assertFalse(GenericUtil.typeMatched(gatOfUnknown, objArrayClass),
                "GAT of unknown component vs Object[] should fail (genericComponentClass == null)");
    }

    @Test
    @DisplayName("5. WildcardType Matching (matchWildcardType & matchWildcardToBounds)")
    void testWildcardTypeMatching() throws Exception {
        WildcardType unbound = getUnboundWildcard(); // ?
        WildcardType extNum = getExtendsWildcard(); // ? extends Number
        WildcardType supInt = getSuperWildcard(); // ? super Integer

        // 1. Wildcard vs Wildcard (Mismatch) -> Covers actualType instanceof WildcardType return false
        assertFalse(GenericUtil.typeMatched(extNum, supInt), "? extends Number vs ? super Integer should not match");

        // 2. Wildcard vs Class (Upper Bound Match) -> Covers !boundClass.isAssignableFrom(s1) false path
        assertTrue(GenericUtil.typeMatched(extNum, Integer.class), "? extends Number vs Integer should match");

        // 3. Wildcard vs Class (Upper Bound Mismatch) -> Covers !boundClass.isAssignableFrom(s1) true path
        assertFalse(GenericUtil.typeMatched(extNum, String.class), "? extends Number vs String should not match");

        // 4. Wildcard vs Class (Lower Bound Match) -> Covers !s1.isAssignableFrom(boundClass) false path
        assertTrue(GenericUtil.typeMatched(supInt, Number.class), "? super Integer vs Number should match");

        // 5. Wildcard vs Class (Lower Bound Mismatch) -> Covers !s1.isAssignableFrom(boundClass) true path
        assertFalse(GenericUtil.typeMatched(supInt, String.class), "? super Integer vs String should not match");

        // 6. Unbound Wildcard (?) vs Class (Should always match as upper bound is Object)
        assertTrue(GenericUtil.typeMatched(unbound, String.class), "? vs String should match (unbound)");

        // 7. Wildcard vs TypeVariable (Mismatch) -> Covers actualClass == null
        assertFalse(GenericUtil.typeMatched(extNum, getTestTypeVariable()), "? extends Number vs T should not match");
    }

    @Test
    @DisplayName("6. Extract Raw Class Coverage (extractRawClass)")
    void testExtractRawClassCoverage() throws Exception {
        // 1. Class -> Covers if (type instanceof Class)
        assertTrue(GenericUtil.typeMatched(String.class, String.class), "Class extraction tested by base cases.");

        // 2. ParameterizedType -> Covers if (type instanceof ParameterizedType)
        Type listString = getTypeFromField("pTypeField"); // List<String>
        Type rawList = getParameterizedType(ArrayList.class, String.class);
        assertFalse(GenericUtil.typeMatched(listString, rawList), "PType extraction tested by PType matches.");

        // 3. GenericArrayType -> Covers if (type instanceof GenericArrayType)
        Type gArray = getTypeFromField("gArrayField"); // T[]
        assertTrue(GenericUtil.typeMatched(gArray, gArray), "GAT extraction tested by GAT matches.");

        // 4. WildcardType -> Covers if (type instanceof WildcardType)
        WildcardType unbound = getUnboundWildcard(); // ?
        assertTrue(GenericUtil.typeMatched(unbound, String.class), "Wildcard extraction tested by Wildcard matches.");

        // 5. TypeVariable -> Covers if (type instanceof TypeVariable)
        Type tVar = getTestTypeVariable(); // T extends Number
        // extractRawClass should resolve T to Number.class
        assertTrue(GenericUtil.typeMatched(tVar, tVar), "TypeVariable extraction tested by TVar matches.");

        // 6. TypeVariable without bounds (S) -> Covers bounds.length == 0
        Type sVar = getMethodTypeVariable(); // S
        assertTrue(GenericUtil.typeMatched(sVar, sVar), "TypeVariable extraction (unbound) tested by TVar matches.");

        // 7. Unknown Type (Mismatch for dispatchTypeMatch final return)
        class UnknownType implements Type {
        }
        assertFalse(GenericUtil.typeMatched(new UnknownType(), String.class), "Unknown type should not match (dispatchTypeMatch default)");
    }

    // --- Inner Class & Other Helper Coverage ---

    @Test
    @DisplayName("7. extractArrayClass Coverage")
    void testExtractArrayClassCoverage() throws Exception {
        // 1. GAT with TypeVariable component (T[]) -> Covers componentClass != null
        Type gArray = getTypeFromField("gArrayField");
        Class<?> rawClass = extractRawClassInternal(gArray);
        assertTrue(rawClass.isArray(), "Extracted T[] should be an array");
        assertTrue(rawClass.getComponentType().equals(Number.class), "T[] component should resolve to Number.class");

        // 2. GAT with Class component (String[]) -> Already covered implicitly.

        // 3. GAT where component is null (Path coverage, though unlikely)
        GenericArrayType badGat = new GenericArrayType() {
            @Override
            public Type getGenericComponentType() {
                // Return a Type that extractRawClass returns null for (e.g., an unknown Type implementation)
                return new Type() {
                };
            }
        };
        Class<?> fallback = extractRawClassInternal(badGat);
        assertTrue(fallback.equals(Object[].class), "Failed component extraction should fallback to Object[].class");
    }

    @Test
    @DisplayName("8. Wildcard vs Unknown Type (actualClass == null coverage)")
    void testWildcardVsUnknownType() throws Exception {
        WildcardType extNum = getExtendsWildcard(); // ? extends Number

        // 创建一个 extractRawClass 会返回 null 的 Type 实例
        Type unknownType = new Type() {
            @Override
            public String toString() {
                return "UnknownType";
            }
        };

        // 此时 actualClass == null，应该返回 false
        assertFalse(GenericUtil.typeMatched(extNum, unknownType),
                "Wildcard vs Unknown Type should fail (actualClass == null)");
    }

    // Direct call to the private helper for coverage
    private Class<?> extractRawClassInternal(Type type) throws Exception {
        Method method = GenericUtil.class.getDeclaredMethod("extractRawClass", Type.class);
        method.setAccessible(true);
        return (Class<?>) method.invoke(null, type);
    }
}
