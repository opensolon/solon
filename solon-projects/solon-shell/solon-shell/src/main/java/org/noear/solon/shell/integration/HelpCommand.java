package org.noear.solon.shell.integration;

import org.noear.solon.annotation.Component;
import org.noear.solon.shell.annotation.Command;

import static org.noear.solon.shell.integration.ShellPlugin.COMMAND_REPOSITORY;

@Component
public class HelpCommand {
    @Command(value = "help", description = "展示所有可用命令")
    public String hellp() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("Solon Shell  - 可用命令列表\n");
        sb.append("=====================================\n");
        COMMAND_REPOSITORY.forEach((cmdName, cmdMeta) -> {
            sb.append(cmdName).append(" - ").append(cmdMeta.getCommandDescription()).append("\n");
        });
        sb.append("=====================================\n");
        sb.append("输入 'exit' 退出终端\n");

        return sb.toString();
    }
}