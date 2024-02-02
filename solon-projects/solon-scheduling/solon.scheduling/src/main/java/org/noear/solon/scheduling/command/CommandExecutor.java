package org.noear.solon.scheduling.command;

/**
 * 命令执行器
 *
 * @author noear
 * @since 2.7
 */
public interface CommandExecutor {
    /**
     * 执行
     *
     * @param command 命令
     */
    void execute(String command) throws Throwable;
}
