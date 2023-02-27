/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.solon.boot;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.utils.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 负责创建 Hasor。
 * @version : 2020年02月27日
 * @author 赵永春 (zyc@hasor.net)
 */
public class BuildConfig {
    private static BuildConfig instance;
    public static BuildConfig getInstance() {
        if(instance == null){
            instance = new BuildConfig();
        }

        return instance;
    }



    public String              mainConfig       = null; // 主配置文件
    public Properties          envProperties    = null; // 1st,来自 EnvironmentAware 接口的 K/V
    public Properties          refProperties    = null; // 2st,通过 refProperties 配置的 K/V
    public Map<Object, Object> customProperties = null; // 3st,利用 property 额外扩充的 K/V
    public boolean             useProperties    = true; // 是否把属性导入到Settings
    private Set<Module>        loadModules      = null; // 要加载的模块

    private Set<Class<?>> needCheckRepeat = new HashSet<>();

    public BuildConfig() {
        this.customProperties = new HashMap<>();
        this.loadModules = new LinkedHashSet<>();

        this.envProperties = Solon.cfg();
    }

    public void addModules(Module module) {
        if (needCheckRepeat.contains(module.getClass())) {
            return;
        } else {
            needCheckRepeat.add(module.getClass());
        }

        loadModules.add(module);
    }

    public AppContext build(Object parentObject) throws IOException {
        Hasor hasorBuild = (parentObject == null) ? Hasor.create() : Hasor.create(parentObject);
        hasorBuild.parentClassLoaderWith(BuildConfig.class.getClassLoader());
        //
        // make sure mainConfig
        String config = this.mainConfig;
        if (!StringUtils.isBlank(config)) {
            //config = SystemPropertyUtils.resolvePlaceholders(config);
            URL resource = ResourceUtil.getResource(config); //StringUtils.isNotBlank(config) ? applicationContext.getResource(config) : null;
            if (resource != null) {
                hasorBuild.mainSettingWith(resource);
            }
        }
        //
        // merge Properties
        if (this.envProperties != null) {
            this.envProperties.forEach((k, v) -> {
                hasorBuild.addVariable(k.toString(), v.toString());
            });
        }
        if (this.refProperties != null) {
            this.refProperties.forEach((k, v) -> {
                hasorBuild.addVariable(k.toString(), v.toString());
            });
        }
        if (this.customProperties != null) {
            this.customProperties.forEach((k, v) -> {
                hasorBuild.addVariable(k.toString(), v.toString());
            });
        }
        //
        // import Properties to Settings
        if (this.useProperties) {
            hasorBuild.importVariablesToSettings();
        }
        //
        return hasorBuild.addModules(new ArrayList<>(this.loadModules)).build();
    }
}
