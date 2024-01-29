package org.noear.solon.command.integration;

import org.noear.solon.Solon;
import org.noear.solon.command.CommandExecutor;
import org.noear.solon.command.annotation.Command;
import org.noear.solon.core.BeanBuilder;
import org.noear.solon.core.BeanWrap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.7
 */
public class CommandBuilder implements BeanBuilder<Command> {
    private Map<String, CommandExecutor> executorMap = new LinkedHashMap<>();

    @Override
    public void doBuild(Class<?> clz, BeanWrap bw, Command anno) throws Throwable {
        //构建时，收集命令
        if (bw.raw() instanceof CommandExecutor) {
            executorMap.put(anno.value(), bw.get());
        }
    }

    //开始执行
    public void exec() throws Throwable {
        for (Map.Entry<String, CommandExecutor> kv : executorMap.entrySet()) {
            if (Solon.cfg().argx().containsKey(kv.getKey())) {
                kv.getValue().execute();
            }
        }
    }
}
