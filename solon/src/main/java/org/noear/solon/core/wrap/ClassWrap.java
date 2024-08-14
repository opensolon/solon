/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.core.wrap;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ReflectUtil;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 类包装，用于缓存类的方法和字段等相关信息
 *
 * @author noear
 * @since 1.0
 * */
public class ClassWrap {
    private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap<>();

    /**
     * 根据clz获取一个ClassWrap
     */
    public static ClassWrap get(Class<?> clz) {
        ClassWrap cw = cached.get(clz);
        if (cw == null) {
            Utils.locker().lock();

            try {
                cw = cached.get(clz);
                if (cw == null) {
                    cw = new ClassWrap(clz);
                    cached.put(clz, cw);
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return cw;
    }

    //clz //与函数同名，_开头
    private final Class<?> _clz;
    //clz.methodS
    private final Method[] declaredMethods;
    private final Method[] methods;
    //clz.fieldS
    private final List<FieldWrap> declaredFieldWraps;
    //clz.all_fieldS
    private final Map<String, FieldWrap> fieldWrapsMap;

    //for record
    private boolean _recordable;
    private Constructor _recordConstructor;
    private List<ParamWrap> _recordParams;

    protected ClassWrap(Class<?> clz) {
        _clz = clz;
        _recordable = true;

        //自己申明的函数
        declaredMethods = ReflectUtil.getDeclaredMethods(clz);
        methods = ReflectUtil.getMethods(clz);

        //所有字段的包装（自己的 + 父类的）

        declaredFieldWraps = new ArrayList<>();
        fieldWrapsMap = new LinkedHashMap<>();

        //扫描所有字段
        doScanAllFields(clz, fieldWrapsMap::containsKey, fieldWrapsMap::put);

        //自己申明的字段
        for (Field f : ReflectUtil.getDeclaredFields(clz)) {
            FieldWrap fw = fieldWrapsMap.get(f.getName());
            if (fw != null) {
                declaredFieldWraps.add(fw);
            }
        }

        if (declaredFieldWraps.size() == 0) {
            _recordable = false;
        }

        if (_recordable) {
            //如果合字段只读
            Constructor<?>[] tmp = clz.getDeclaredConstructors();
            _recordConstructor = tmp[tmp.length - 1];
            _recordParams = new ArrayList<>();
            for (Parameter p : _recordConstructor.getParameters()) {
                _recordParams.add(new ParamWrap(p));
            }
        }
    }

    public Class<?> clz() {
        return _clz;
    }

    /**
     * 获取所有字段的包装（含超类）
     */
    public Map<String, FieldWrap> getFieldWraps() {
        return Collections.unmodifiableMap(fieldWrapsMap);
    }

    /**
     * @deprecated 2.5
     */
    @Deprecated
    public Map<String, FieldWrap> getFieldAllWraps() {
        return getFieldWraps();
    }

    public FieldWrap getFieldWrap(String field) {
        return fieldWrapsMap.get(field);
    }

    /**
     * 获取自己申明的Method
     */
    public Method[] getDeclaredMethods() {
        return declaredMethods;
    }

    /**
     * 获取所有公有的Method
     */
    public Method[] getMethods() {
        return methods;
    }

    /**
     * 是否为 record
     */
    public boolean recordable() {
        return _recordable;
    }

    /**
     * record 构建函数（可能为 null）
     */
    public Constructor recordConstructor() {
        return _recordConstructor;
    }

    /**
     * record 构造参数（可能为 null）
     */
    public List<ParamWrap> recordParams() {
        return _recordParams;
    }

    /**
     * 新建实例
     *
     * @param data 填充数据
     */
    public <T> T newBy(Properties data) {
        try {
            Constructor constructor = clz().getConstructor(Properties.class);
            if (constructor != null) {
                return (T) constructor.newInstance(data);
            }
        } catch (Throwable e) {
        }

        return newBy(data::getProperty);
    }


    public <T> T newBy(Function<String, String> data) {
        try {
            return newBy(data, null);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * 新建实例
     *
     * @param data 填充数据
     * @param ctx  上下文
     */
    public <T> T newBy(Function<String, String> data, Context ctx) throws Exception {
        if (recordable()) {
            //for record
            List<ParamWrap> argsP = recordParams();
            Object[] argsV = new Object[argsP.size()];

            for (int i = 0; i < argsP.size(); i++) {
                ParamWrap p = argsP.get(i);
                String key = p.getName();
                String val0 = data.apply(key);

                if (val0 != null) {
                    //将 string 转为目标 type，并为字段赋值
                    Object val = ConvertUtil.to(p, val0, ctx);
                    argsV[i] = val;
                } else {
                    if (p.getType() == UploadedFile.class) {
                        argsV[i] = ctx.file(key);//如果是 UploadedFile
                    } else {
                        argsV[i] = null;
                    }
                }
            }

            Object obj = recordConstructor().newInstance(argsV);
            return (T) obj;
        } else {
            Object obj = ClassUtil.newInstance(clz());

            doFill(obj, data, ctx);

            return (T) obj;
        }
    }

    /**
     * 为实例填充数据
     *
     * @param data 填充数据
     */
    public void fill(Object bean, Function<String, String> data) {
        try {
            doFill(bean, data, null);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 为实例填充数据
     *
     * @param data 填充数据
     * @param ctx  上下文
     */
    private void doFill(Object bean, Function<String, String> data, Context ctx) throws Exception {
        if (fieldWrapsMap.isEmpty() && NativeDetector.inNativeImage()) {
            LogUtil.global().warn(String.format("Class: %s don't have any field, can't fill data. you should use: nativeMetadata.registerField(field) at aot runtime.", _clz.getName()));
        }
        for (Map.Entry<String, FieldWrap> kv : fieldWrapsMap.entrySet()) {
            String key = kv.getKey();
            String val0 = data.apply(key);

            FieldWrap fw = kv.getValue();

            if (val0 != null) {
                //将 string 转为目标 type，并为字段赋值
                Object val = ConvertUtil.to(fw.getDescriptor(), val0, ctx);
                fw.setValue(bean, val);
            } else {
                if (ctx != null && fw.type == UploadedFile.class) {
                    UploadedFile file1 = ctx.file(key);
                    if (file1 != null) {
                        fw.setValue(bean, file1);
                    }
                }
            }
        }
    }

    /**
     * 扫描一个类的所有字段（不能与Snack3的复用；它需要排除非序列化字段）
     */
    private void doScanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
        if (clz == null) {
            return;
        }

        for (Field f : ReflectUtil.getDeclaredFields(clz)) {
            int mod = f.getModifiers();

            if (!Modifier.isStatic(mod)) {
                if (checker.test(f.getName()) == false) {
                    _recordable &= Modifier.isFinal(mod);
                    //使用当前类，而不是申明类！
                    consumer.accept(f.getName(), new FieldWrap(_clz, f, Modifier.isFinal(mod)));
                }
            }
        }

        Class<?> sup = clz.getSuperclass();
        if (sup != Object.class) {
            doScanAllFields(sup, checker, consumer);
        }
    }
}