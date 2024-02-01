package demo;

import org.noear.solon.Solon;
import org.noear.solon.command.CommandExecutor;
import org.noear.solon.command.annotation.Command;

/**
 * @author noear 2024/2/1 created
 */
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
