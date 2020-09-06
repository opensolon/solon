package org.noear.solon.extend.validation.annotation;

import org.noear.solon.annotation.XNote;

public @interface Max {
    /**
     * param names
     * */
    @XNote("param names")
    String[] value();

    long max();
}
