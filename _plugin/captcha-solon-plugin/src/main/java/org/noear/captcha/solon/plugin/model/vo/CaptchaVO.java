package org.noear.captcha.solon.plugin.model.vo;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * @memo
 */
public class CaptchaVO implements Serializable {

    /**
     * 验证码id(后台申请)
     */
    private String captchaId;

    private String projectCode;

    /**
     * 验证码类型:(clickWord,blockPuzzle)
     */
    private String captchaType;

    private String captchaOriginalPath;

    private String captchaFontType;

    private Integer captchaFontSize;

    /**
     * 原生图片base64
     */
    private String originalImageBase64;

    /**
     * 滑块点选坐标
     */
    private Point point;

    /**
     * 滑块图片base64
     */
    private String jigsawImageBase64;

    /**
     * 点选文字
     */
    private List<String> wordList;

    /**
     * 点选坐标
     */
    private List<Point> pointList;


    /**
     * 点坐标(base64加密传输)
     */
    private String pointJson;


    /**
     * UUID(每次请求的验证码唯一标识)
     */
    private String token;

    /**
     * 校验结果
     */
    private Boolean result = false;

    /**
     * 后台二次校验参数
     */
    private String captchaVerification;


    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        this.captchaType = captchaType;
    }

    public String getCaptchaOriginalPath() {
        return captchaOriginalPath;
    }

    public void setCaptchaOriginalPath(String captchaOriginalPath) {
        this.captchaOriginalPath = captchaOriginalPath;
    }

    public String getCaptchaFontType() {
        return captchaFontType;
    }

    public void setCaptchaFontType(String captchaFontType) {
        this.captchaFontType = captchaFontType;
    }

    public Integer getCaptchaFontSize() {
        return captchaFontSize;
    }

    public void setCaptchaFontSize(Integer captchaFontSize) {
        this.captchaFontSize = captchaFontSize;
    }

    public String getOriginalImageBase64() {
        return originalImageBase64;
    }

    public void setOriginalImageBase64(String originalImageBase64) {
        this.originalImageBase64 = originalImageBase64;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getJigsawImageBase64() {
        return jigsawImageBase64;
    }

    public void setJigsawImageBase64(String jigsawImageBase64) {
        this.jigsawImageBase64 = jigsawImageBase64;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public String getPointJson() {
        return pointJson;
    }

    public void setPointJson(String pointJson) {
        this.pointJson = pointJson;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getCaptchaVerification() {
        return captchaVerification;
    }

    public void setCaptchaVerification(String captchaVerification) {
        this.captchaVerification = captchaVerification;
    }
}
