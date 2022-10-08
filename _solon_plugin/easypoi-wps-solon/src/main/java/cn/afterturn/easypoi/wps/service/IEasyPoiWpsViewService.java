package cn.afterturn.easypoi.wps.service;

import cn.afterturn.easypoi.wps.entity.WpsFileEntity;
import cn.afterturn.easypoi.wps.entity.WpsToken;
import cn.afterturn.easypoi.wps.entity.resreq.WpsFileHistoryRequest;
import cn.afterturn.easypoi.wps.entity.resreq.WpsFileHistoryResponse;
import cn.afterturn.easypoi.wps.entity.resreq.WpsFileResponse;
import cn.afterturn.easypoi.wps.entity.resreq.WpsUserRequest;
import org.noear.solon.core.handle.UploadedFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 获取
 *
 * @author jueyue on 20-5-7.
 */
public interface IEasyPoiWpsViewService extends IEasyPoiWpsService {
    /**
     * 获取Wps的地址
     *
     * @param filePath
     * @return
     */
    default WpsToken getViewUrl(String filePath) {
        WpsToken t = new WpsToken();
        Map<String, String> values = new HashMap<String, String>() {
            {
                put("_w_appid", getAppId());
                put("_w_userid", getUserId());
                put("_w_filepath", filePath);
            }
        };
        String keyValueStr  = EasyPoiWpsUtil.getKeyValueStr(values);
        String signature    = EasyPoiWpsUtil.getSignature(values, getAppSecret());
        String fileTypeCode = EasyPoiWpsFileUtil.getFileTypeCode(EasyPoiWpsFileUtil.getFileTypeByPath(filePath));

        String wpsUrl = getDomain() + fileTypeCode + "/" + getFileId(filePath) + "?"
                + keyValueStr + "_w_signature=" + signature;
        t.setToken(getToken());
        t.setExpires_in(600);
        t.setWpsUrl(wpsUrl);

        return t;
    }

    /**
     * 获取用户Token有必要可以带上
     *
     * @return
     */
    default String getToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取文件ID,如果filePath可以现实没必要,如果使用ID请实现
     *
     * @param filePath
     * @return
     */
    default String getFileId(String filePath) {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * WPS路径
     *
     * @return
     */
    default String getDomain() {
        return "https://wwo.wps.cn/office/";
    }

    /**
     * 获取当前用户ID,如果不权限校验可以不实现
     *
     * @return
     */
    default String getUserId() {
        return "0";
    }

    /**
     * 获取文件信息
     *
     * @param userId
     * @param filepath
     * @param fileId
     * @return 返回文件信息
     */
    default WpsFileResponse getFileInfo(String userId, String filepath, String fileId) {
        WpsFileResponse response   = new WpsFileResponse();
        WpsFileEntity   fileEntity = new WpsFileEntity();
        fileEntity.setId(fileId);
        fileEntity.setName(getFileName(filepath));
        fileEntity.setSize(getFileSize(filepath));
        fileEntity.setDownload_url(getDownLoadUrl(filepath));
        response.setFile(fileEntity);
        return response;
    }

    /**
     * 单位为B
     *
     * @param filepath
     * @return
     */
    int getFileSize(String filepath);

    /**
     * 获取文件的下载路径
     *
     * @param filepath
     * @return
     */
    String getDownLoadUrl(String filepath);

    /**
     * 获取文件名称 -中文或者英文
     *
     * @param filepath
     * @return
     */
    default String getFileName(String filepath) {
        return EasyPoiWpsFileUtil.getFileName(filepath);
    }

    /**
     * 文件被那些客户协作
     *
     * @param fileId
     * @param list
     */
    default void fileOnline(String fileId, WpsUserRequest list) {
    }

    /**
     * 保存文件
     *
     * @param fileId
     * @param userId
     * @param file
     * @return
     */
    default WpsFileEntity fileSave(String fileId, String userId, UploadedFile file) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取特定版本的文件
     *
     * @param fileId
     * @param filepath
     * @param version
     * @return
     */
    default WpsFileEntity getFileInfoOfVersion(String fileId, String filepath, Integer version) {
        WpsFileEntity fileEntity = new WpsFileEntity();
        fileEntity.setId(fileId);
        fileEntity.setName(getFileName(filepath));
        fileEntity.setDownload_url(getDownLoadUrl(filepath, version));
        fileEntity.setModifier(fileEntity.getModifier());
        fileEntity.setModify_time(System.currentTimeMillis());
        return fileEntity;
    }

    /**
     * 获取特定版本的URL
     *
     * @param filepath
     * @param version
     * @return
     */
    default String getDownLoadUrl(String filepath, Integer version) {
        throw new UnsupportedOperationException();
    }

    /**
     * 文件重命名
     *
     * @param fileId
     * @param userId
     * @param name
     */
    default void rename(String fileId, String userId, String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取文件历史版本
     *
     * @param req
     * @return
     */
    default WpsFileHistoryResponse getHistory(WpsFileHistoryRequest req) {
        throw new UnsupportedOperationException();
    }
}
