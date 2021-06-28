package webapp.demo2_mvc;

import org.noear.solon.extend.aspect.annotation.Service;

/**
 * @author noear 2021/6/28 created
 */
@Service
public class CmdServiceImpl implements CmdService{
    public String name(String name) {
        return name;
    }
}
