package org.noear.solon.scheduling.command;

import org.noear.solon.Solon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 命令管理
 *
 * @author noear
 * @since 2.7
 */
public class CommandManager {
    private static Logger log = LoggerFactory.getLogger(CommandManager.class);

    public static CommandManager getInstance() {
        //方便在单测环境下切换 SolonApp，可以相互独立
        return Solon.context().attachmentOf(CommandManager.class, CommandManager::new);
    }


    private Map<String, CommandExecutor> executorMap = new LinkedHashMap<>();

    /**
     * 注册
     *
     * @param command 命令
     * @param executor 执行器
     */
    public void register(String command, CommandExecutor executor) {
        executorMap.put(command, executor);
    }

    /**
     * 是否存在
     *
     * @param command 命令
     */
    public boolean exists(String command) {
        return executorMap.containsKey(command);
    }

    /**
     * 执行命令
     *
     * @param command 命令
     */
    public void execute(String command) throws Throwable {
        CommandExecutor executor = executorMap.get(command);
        if (executor != null) {
            executor.execute(command);
        }
    }

    /**
     * 执行全部命令
     */
    public void executeAll() {
        for (Map.Entry<String, CommandExecutor> kv : executorMap.entrySet()) {
            if (Solon.cfg().argx().containsKey(kv.getKey())) {
                try {
                    kv.getValue().execute(kv.getKey());
                } catch (Throwable e) {
                    log.warn("Command execute failed, cmd={}", kv.getKey(), e);
                }
            }
        }
    }
}
