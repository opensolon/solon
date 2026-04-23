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
package features.serialization.javabin;

import org.junit.jupiter.api.Test;
import org.noear.solon.serialization.javabin.JavabinClassFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassFilterTest {

    @Test
    public void defaultsAllowCommonJdk() {
        JavabinClassFilter f = JavabinClassFilter.defaults();
        assertTrue(f.isAllowed("java.lang.String"));
        assertTrue(f.isAllowed("java.lang.Integer"));
        assertTrue(f.isAllowed("java.util.ArrayList"));
        assertTrue(f.isAllowed("java.util.HashMap"));
        assertTrue(f.isAllowed("java.util.concurrent.ConcurrentHashMap"));
        assertTrue(f.isAllowed("java.time.LocalDate"));
        assertTrue(f.isAllowed("java.math.BigDecimal"));
    }

    @Test
    public void defaultsDenyKnownGadgetRoots() {
        JavabinClassFilter f = JavabinClassFilter.defaults();
        assertFalse(f.isAllowed("java.lang.Runtime"));
        assertFalse(f.isAllowed("java.lang.ProcessBuilder"));
        assertFalse(f.isAllowed("javax.management.BadAttributeValueExpException"));
        assertFalse(f.isAllowed("javax.naming.InitialContext"));
        assertFalse(f.isAllowed("javax.script.ScriptEngineManager"));
        assertFalse(f.isAllowed("com.sun.rowset.JdbcRowSetImpl"));
        assertFalse(f.isAllowed("sun.reflect.annotation.AnnotationInvocationHandler"));
    }

    @Test
    public void defaultsDenyCustomPojo() {
        JavabinClassFilter f = JavabinClassFilter.defaults();
        assertFalse(f.isAllowed("com.acme.Order"));
    }

    @Test
    public void allowPackagePrefix() {
        JavabinClassFilter f = new JavabinClassFilter();
        f.allow("com.acme.");
        assertTrue(f.isAllowed("com.acme.Order"));
        assertTrue(f.isAllowed("com.acme.billing.Invoice"));
        assertFalse(f.isAllowed("com.acmex.NotOurs"));
    }

    @Test
    public void allowExactClassAndInnerClass() {
        JavabinClassFilter f = new JavabinClassFilter();
        f.allow("com.acme.Order");
        assertTrue(f.isAllowed("com.acme.Order"));
        assertTrue(f.isAllowed("com.acme.Order$Builder"));
        assertFalse(f.isAllowed("com.acme.OrderAlike"));
        assertFalse(f.isAllowed("com.acme.OtherOrder"));
    }

    @Test
    public void denyBeatsAllow() {
        JavabinClassFilter f = new JavabinClassFilter();
        f.allow("com.acme.");
        f.deny("com.acme.internal.");
        assertTrue(f.isAllowed("com.acme.Order"));
        assertFalse(f.isAllowed("com.acme.internal.Secret"));
    }

    @Test
    public void unrestrictedAllowsAll() {
        JavabinClassFilter f = JavabinClassFilter.unrestricted();
        assertTrue(f.isAllowed("com.any.Class"));
        assertTrue(f.isAllowed("java.lang.String"));
    }

    @Test
    public void unrestrictedStillHonorsDeny() {
        JavabinClassFilter f = JavabinClassFilter.unrestricted();
        f.deny("com.acme.internal.");
        assertTrue(f.isAllowed("com.any.Class"));
        assertFalse(f.isAllowed("com.acme.internal.Secret"));
    }

    @Test
    public void allowAllToggle() {
        JavabinClassFilter f = new JavabinClassFilter();
        assertFalse(f.isAllowed("com.acme.Any"));
        f.allowAll(true);
        assertTrue(f.isAllowed("com.acme.Any"));
        f.allowAll(false);
        assertFalse(f.isAllowed("com.acme.Any"));
    }

    @Test
    public void arrayOfAllowedClassIsAllowed() {
        JavabinClassFilter f = JavabinClassFilter.defaults();
        assertTrue(f.isAllowed("[Ljava.lang.String;"));
        assertTrue(f.isAllowed("[[Ljava.lang.String;"));
    }

    @Test
    public void arrayOfDeniedClassIsDenied() {
        JavabinClassFilter f = JavabinClassFilter.defaults();
        assertFalse(f.isAllowed("[Ljava.lang.Runtime;"));
    }

    @Test
    public void primitiveArraysAllowed() {
        JavabinClassFilter f = new JavabinClassFilter();
        assertTrue(f.isAllowed("[I"));
        assertTrue(f.isAllowed("[B"));
        assertTrue(f.isAllowed("[J"));
    }

    @Test
    public void nullOrEmptyDenied() {
        JavabinClassFilter f = JavabinClassFilter.defaults();
        assertFalse(f.isAllowed(null));
        assertFalse(f.isAllowed(""));
    }
}
