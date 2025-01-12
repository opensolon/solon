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
package org.noear.solon.proxy.asm;

import org.noear.solon.Utils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TargetClassVisitor extends ClassVisitor {
    static final Logger log = LoggerFactory.getLogger(TargetClassVisitor.class);

    private boolean isFinal;
    private List<MethodDigest> methods = new ArrayList<>();
    private List<MethodDigest> declaredMethods = new ArrayList<>();
    private List<MethodDigest> constructors = new ArrayList<>();

    private final ClassLoader classLoader;

    public TargetClassVisitor(ClassLoader classLoader, ClassReader reader) {
        super(AsmProxy.ASM_VERSION);
        this.classLoader = classLoader;

        reader.accept(this, ClassReader.SKIP_DEBUG);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL) {
            isFinal = true;
        }

        if (superName != null) {
            List<MethodDigest> beans = initMethodBeanByParent(superName);

            if (Utils.isNotEmpty(beans)) {
                for (MethodDigest bean : beans) {
                    if (!methods.contains(bean)) {
                        methods.add(bean);
                    }
                }
            }
        }

        if (interfaces != null) {
            for (String ifType : interfaces) {
                List<MethodDigest> beans = initMethodBeanByParent(ifType);

                if (Utils.isNotEmpty(beans)) {
                    for (MethodDigest bean : beans) {
                        if (!methods.contains(bean)) {
                            methods.add(bean);
                        }
                    }
                }
            }
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            // 构造方法
            MethodDigest constructor = new MethodDigest(access, name, descriptor);
            constructors.add(constructor);
        } else if (!"<clinit>".equals(name)) {
            // 其他方法
            if (MethodFinder.allowMethod(access)) {
                MethodDigest methodDigest = new MethodDigest(access, name, descriptor);

                //public 给 declaredMethods + methods
//                if (declaredMethods.contains(methodDigest) == false) {
//                    declaredMethods.add(methodDigest);
//                }

                if (methods.contains(methodDigest) == false) {
                    methods.add(methodDigest);
                }
            }
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public List<MethodDigest> getMethods() {
        return methods;
    }

    public List<MethodDigest> getDeclaredMethods() {
        return declaredMethods;
    }

    public List<MethodDigest> getConstructors() {
        return constructors;
    }

    private List<MethodDigest> initMethodBeanByParent(String superName) {
        try {
            if (Utils.isNotEmpty(superName)) {
                if (superName.equals("java/lang/Object")) {
                    return null;
                }

                URL superNameUrl = classLoader.getResource(superName.replace('.', '/') + ".class");
                if (superNameUrl == null) {
                    throw new IOException("Class not found: " + superName);
                }

                ClassReader reader;
                try (InputStream in = superNameUrl.openStream()) {
                    reader = new ClassReader(in);
                }

                TargetClassVisitor visitor = new TargetClassVisitor(classLoader, reader);

                List<MethodDigest> digests = new ArrayList<>();

                for (MethodDigest methodDigest : visitor.methods) {
                    if (MethodFinder.allowMethod(methodDigest.access)) {
                        digests.add(methodDigest);
                    }
                }

                return digests;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return null;
    }
}