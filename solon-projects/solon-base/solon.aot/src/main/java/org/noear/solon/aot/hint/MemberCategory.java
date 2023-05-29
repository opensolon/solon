package org.noear.solon.aot.hint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Represent predefined {@linkplain Member members} groups.
 *
 * @author Andy Clement
 * @author Sebastien Deleuze
 * @author Stephane Nicoll
 * @since 2.2
 */
public enum MemberCategory {

    /**
     * A category that represents public {@linkplain Field fields}.
     *
     * @see Class#getFields()
     */
    PUBLIC_FIELDS,

    /**
     * A category that represents {@linkplain Class#getDeclaredFields() declared
     * fields}, that is all fields defined by the class, but not inherited ones.
     *
     * @see Class#getDeclaredFields()
     */
    DECLARED_FIELDS,

    /**
     * A category that defines public {@linkplain Constructor constructors} can
     * be introspected, but not invoked.
     *
     * @see Class#getConstructors()
     * @see ExecutableMode#INTROSPECT
     */
    INTROSPECT_PUBLIC_CONSTRUCTORS,

    /**
     * A category that defines {@linkplain Class#getDeclaredConstructors() all
     * constructors} can be introspected, but not invoked.
     *
     * @see Class#getDeclaredConstructors()
     * @see ExecutableMode#INTROSPECT
     */
    INTROSPECT_DECLARED_CONSTRUCTORS,

    /**
     * A category that defines public {@linkplain Constructor constructors} can
     * be invoked.
     *
     * @see Class#getConstructors()
     * @see ExecutableMode#INVOKE
     */
    INVOKE_PUBLIC_CONSTRUCTORS,

    /**
     * A category that defines {@linkplain Class#getDeclaredConstructors() all
     * constructors} can be invoked.
     *
     * @see Class#getDeclaredConstructors()
     * @see ExecutableMode#INVOKE
     */
    INVOKE_DECLARED_CONSTRUCTORS,

    /**
     * A category that defines public {@linkplain Method methods}, including
     * inherited ones can be introspect, but not invoked.
     *
     * @see Class#getMethods()
     * @see ExecutableMode#INTROSPECT
     */
    INTROSPECT_PUBLIC_METHODS,

    /**
     * A category that defines {@linkplain Class#getDeclaredMethods() all
     * methods}, excluding inherited ones can be introspected, but not invoked.
     *
     * @see Class#getDeclaredMethods()
     * @see ExecutableMode#INTROSPECT
     */
    INTROSPECT_DECLARED_METHODS,

    /**
     * A category that defines public {@linkplain Method methods}, including
     * inherited ones can be invoked.
     *
     * @see Class#getMethods()
     * @see ExecutableMode#INVOKE
     */
    INVOKE_PUBLIC_METHODS,

    /**
     * A category that defines {@linkplain Class#getDeclaredMethods() all
     * methods}, excluding inherited ones can be invoked.
     *
     * @see Class#getDeclaredMethods()
     * @see ExecutableMode#INVOKE
     */
    INVOKE_DECLARED_METHODS,

    /**
     * A category that represents public {@linkplain Class#getClasses() inner
     * classes}. Contrary to other categories, this does not register any
     * particular reflection for them but rather make sure they are available
     * via a call to {@link Class#getClasses}.
     */
    PUBLIC_CLASSES,

    /**
     * A category that represents all {@linkplain Class#getDeclaredClasses()
     * inner classes}. Contrary to other categories, this does not register any
     * particular reflection for them but rather make sure they are available
     * via a call to {@link Class#getDeclaredClasses}.
     */
    DECLARED_CLASSES;

}
