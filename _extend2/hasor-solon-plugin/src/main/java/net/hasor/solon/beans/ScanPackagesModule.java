package net.hasor.solon.beans;

import net.hasor.core.ApiBinder;
import net.hasor.core.DimModule;
import net.hasor.core.Module;
import net.hasor.core.TypeSupplier;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Predicate;

public class ScanPackagesModule implements Module {
    protected static Logger logger             = LoggerFactory.getLogger(ScanPackagesModule.class);
    private          String[]            loadModulePackages = null;
    private Predicate<Class<?>> include;

    public ScanPackagesModule(String[] packages) {
        this(packages, null);
    }

    public ScanPackagesModule(String[] packages, Predicate<Class<?>> include) {
        this.loadModulePackages = packages;
        this.include = include == null ? Matchers.anyClass() : include;
    }

    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        if (loadModulePackages == null) {
            this.loadModulePackages = apiBinder.getEnvironment().getSpanPackage();
        }
        //
        TypeSupplier typeSupplier = new SolonTypeSupplier().beforeOther(new TypeSupplier() {
            @Override
            public <T> T get(Class<? extends T> targetType) {
                try {
                    return targetType.newInstance();
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
        });
        //
        logger.info("loadModule autoScan='true' scanPackages=" + StringUtils.join(this.loadModulePackages, ","));
        Set<Class<?>> classSet = apiBinder.findClass(DimModule.class, loadModulePackages);
        apiBinder.loadModule(classSet, include, typeSupplier);
    }
}
