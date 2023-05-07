package cn.afterturn.easypoi.wps.controller;

import cn.afterturn.easypoi.wps.entity.WpsToken;
import cn.afterturn.easypoi.wps.entity.resreq.*;
import cn.afterturn.easypoi.wps.service.IEasyPoiWpsViewService;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author jueyue on 20-5-7.
 */
@Controller
@Mapping("easypoi/wps/v1/3rd/file")
public class EasyPoiFileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EasyPoiFileController.class);

    @Inject(required = false)
    private IEasyPoiWpsViewService easyPoiWpsService;

    /**
     * 获取网络文件预览URL
     *
     * @param filePath
     * @return t
     */
    @Get
    @Mapping("getViewUrl")
    public WpsToken getViewUrl(String filePath) {
        LOGGER.info("getViewUrlWebPath：filePath={}", filePath);
        WpsToken token = easyPoiWpsService.getViewUrl(decode(filePath));
        return token;
    }

    /**
     * 获取文件元数据
     */
    @Get
    @Mapping("info")
    public WpsFileResponse getFileInfo(Context ctx, @Param( "_w_userid") String userId,
                                       @Param("_w_filepath") String filePath) {
        LOGGER.info("获取文件元数据userId:{},path:{}", userId, filePath);
        String          fileId       = ctx.header("x-weboffice-file-id");
        WpsFileResponse fileResponse = easyPoiWpsService.getFileInfo(userId, decode(filePath), fileId);
        return fileResponse;
    }

    /**
     * 通知此文件目前有哪些人正在协作
     */
    @Post
    @Mapping("online")
    public WpsResponse fileOnline(Context ctx, @Body WpsUserRequest list) {
        LOGGER.info("通知此文件目前有哪些人正在协作param:{}", list);
        String fileId = ctx.header("x-weboffice-file-id");
        easyPoiWpsService.fileOnline(fileId, list);
        return WpsResponse.success();
    }

    /**
     * 上传文件新版本
     */
    @Post
    @Mapping("save")
    public WpsFileSaveResponse fileSave(Context ctx,
                                        @Param("_w_userid") String userId,
                                        @Body UploadedFile file) {
        LOGGER.info("上传文件新版本");
        String fileId = ctx.header("x-weboffice-file-id");
        return new WpsFileSaveResponse(easyPoiWpsService.fileSave(fileId, userId, file));
    }

    /**
     * 获取特定版本的文件信息
     */
    @Get
    @Mapping("version/{version}")
    public WpsFileSaveResponse fileVersion(Context ctx, Integer version,
                                           @Param("_w_filepath") String filePath) {
        LOGGER.info("获取特定版本的文件信息version:{}", version);
        String fileId = ctx.header("x-weboffice-file-id");
        return new WpsFileSaveResponse(easyPoiWpsService.getFileInfoOfVersion(fileId, decode(filePath), version));
    }

    /**
     * 文件重命名
     */
    @Put
    @Mapping("rename")
    public WpsResponse fileRename(Context ctx, @Body WpsRenameRequest req, @Param("_w_userid") String userId) {
        LOGGER.info("文件重命名param:{},userId:{}", req, userId);
        String fileId = ctx.header("x-weboffice-file-id");
        easyPoiWpsService.rename(fileId, userId, req.getName());
        return WpsResponse.success();
    }

    /**
     * 获取所有历史版本文件信息
     */
    @Post
    @Mapping("history")
    public WpsFileHistoryResponse fileHistory(@Body WpsFileHistoryRequest req) {
        LOGGER.info("获取所有历史版本文件信息param:{}", req);
        return easyPoiWpsService.getHistory(req);
    }

    /**
     * 新建文件
     */
    @Post
    @Mapping("new")
    public WpsResponse fileNew(@Body UploadedFile file, @Param("_w_userid") String userId) {
        LOGGER.info("新建文件,只打印不实现,userid:{}", userId);
        return WpsResponse.success();
    }

    /**
     * 回调通知
     */
    @Post
    @Mapping("onnotify")
    public WpsResponse onNotify(@Body Map obj) {
        LOGGER.info("回调通知,只打印不实现,param:{}", obj);
        return WpsResponse.success();
    }

    public String decode(String filePath) {
        try {
            filePath = URLDecoder.decode(filePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return filePath;
    }

}
