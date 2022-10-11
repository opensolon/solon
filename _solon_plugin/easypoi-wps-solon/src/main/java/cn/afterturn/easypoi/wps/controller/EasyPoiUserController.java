package cn.afterturn.easypoi.wps.controller;

import cn.afterturn.easypoi.wps.entity.WpsUserEntity;
import cn.afterturn.easypoi.wps.entity.resreq.WpsUserRequest;
import cn.afterturn.easypoi.wps.entity.resreq.WpsUserResponse;
import cn.afterturn.easypoi.wps.service.IEasyPoiWpsUserService;
import org.noear.solon.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jueyue on 20-5-8.
 */
@Controller
@Mapping("easypoi/wps/v1/3rd/file")
public class EasyPoiUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EasyPoiUserController.class);

    @Inject
    private IEasyPoiWpsUserService userService;

    /**
     * 获取用户信息
     */
    @Post
    @Mapping("info")
    public WpsUserResponse userInfo(@Body WpsUserRequest list) {
        LOGGER.info("获取用户信息param:{}", list);
        WpsUserResponse response = new WpsUserResponse();
        for (int i = 0; i < list.getIds().size(); i++) {
            if (userService != null) {
                response.getUsers().add(userService.getUser(list.getIds().get(i)));
            } else {
                response.getUsers().add(new WpsUserEntity());
            }
        }
        return response;
    }
}
