package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.XContext;

public class WhitelistCheckerImp implements WhitelistChecker{
    private static WhitelistChecker _global;

    public static WhitelistChecker global() {
        if (_global == null) {
            _global = new WhitelistCheckerImp();
        }

        return _global;
    }

    public static void globalSet(WhitelistChecker checker) {
        if (checker == null) {
            _global = checker;
        }
    }


    @Override
    public boolean check(Whitelist anno, XContext ctx) {
        return false;
    }
}
