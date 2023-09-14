package org.noear.solon.core;

import java.util.regex.Pattern;

/**
 * @author noear
 * @since 1.2
 */
public class Constants {
    public static final String PARM_UNDEFINED_VALUE = "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n";

    public static final String SOLON_ENV = "solon.env";

    public static final String SOLON_ACTIVATE_ON_ENV = "solon.activate.on-env";

    public static final Pattern YML_SPLIT_PATTERN = Pattern.compile("^---\\s?$");
}
