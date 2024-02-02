package demo;

import org.noear.solon.Solon;
import org.noear.solon.scheduling.annotation.Command;
import org.noear.solon.scheduling.annotation.EnableCommand;
import org.noear.solon.scheduling.command.CommandExecutor;

@EnableCommand
@Command("cmd:user")
public class Cmd1 implements CommandExecutor {
    @Override
    public void execute(String command) {
        System.out.println("exec: " + command);
    }

    public static void main(String[] args) {
        Solon.start(Cmd1.class, args);
    }
}
