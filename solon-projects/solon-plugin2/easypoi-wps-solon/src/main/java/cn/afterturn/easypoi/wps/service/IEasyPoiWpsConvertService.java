package cn.afterturn.easypoi.wps.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Wps 转换文件服务
 * @author JueYue
 * @date 2021-05-21-5-25
 * @since 1.0
 */
public interface IEasyPoiWpsConvertService  extends IEasyPoiWpsService {

    final String CONVERT_API = "https://dhs.open.wps.cn/pre/v1/convert" ; //转换请求地址
    final String QUERY_API = "https://dhs.open.wps.cn/pre/v1/query" ; //查询请求地址;

    /** 文件转换
     * WPS接口的文件转换
     * 文档： https://open.wps.cn/docs/doc-format-conversion/access-know
     * 文件原格式	转换后格式
     * word			pdf、png
     * excel		pdf、png
     * ppt			pdf
     * pdf			word、ppt、excel
     * @param taskId 任务ID，需唯一
     * @param srcUri 原文件地址 需要转换的文件地址，保证可打开、下载
     * @param fileName 文件名 好象没什么用，转换后下载的文件名都不是这个的。
     * @param exportType 转换的格式，pdf、png等
     * @param callback  回调地址
     * */
    default boolean fileConvert(String taskId , String srcUri , String fileName , String exportType, String callback) throws Exception {
        String headerDate = EasyPoiWpsUtil.getGMTDate();
        //请求参数
        Map<String, Object> param = Maps.newLinkedHashMap(); //注，使用newLinkedHashMap可让顺序一致，调用API时参数顺序与参与签名时顺序一致。
        param.put("SrcUri", srcUri);
        param.put("FileName", fileName);
        param.put("ExportType", exportType);
        //回调地址，文件转换后的通知地址，需保证可访问；访问不了也没关系，可以再调query接口
        if (callback == null || !callback.startsWith("http")) {
            callback = "http://www.wupaas.com";
        }
        param.put("CallBack", callback);
        param.put("TaskId", taskId);

        //********计算签名
        //Content-MD5 表示请求内容数据的MD5值，对消息内容（不包括头部）计算MD5值获得128比特位数字，对该数字进行base64编码而得到，如”eB5eJF1ptWaXm4bijSPyxw==”，也可以为空；
        String contentMd5 = EasyPoiWpsUtil.getMD5(param) ; //请求内容数据的MD5值

        //Signature = base64(hmac-sha1(AppKey, VERB + “\n” + Content-MD5 + “\n” + Content-Type + “\n” + Date + “\n” + URI))
        //VERB表示HTTP 请求的Method的字符串，可选值有PUT、GET、POST、HEAD、DELETE等；
        String signature = EasyPoiWpsUtil.getSignature("POST" , CONVERT_API , contentMd5 , headerDate, "application/json",getAppSecret()) ;//签名url的参数不带请求参数
        String authorization =  "WPS " + getAppId() + ":" + signature ;
        //header参数
        Map<String, String> headers = Maps.newLinkedHashMap();
        headers.put(HttpHeaders.CONTENT_TYPE ,"application/json" ) ;
        headers.put(HttpHeaders.DATE , headerDate) ;
        headers.put("Content-MD5" , contentMd5) ;//文档上是 "Content-Md5"
        headers.put(HttpHeaders.AUTHORIZATION , authorization) ;
        //调用时用json Body数据提交
        // 内容如{"SrcUri":"http://xxx","FileName":"xxx","ExportType":"pdf","CallBack":"http://xxx/v1/3rd/convertresult","TaskId":"abcd1234"};
        String parsStr = JSONUtil.toJsonStr(param);
        String result = HttpUtil.createPost(CONVERT_API).addHeaders(headers).body(parsStr).execute().body();
        return "OK".equalsIgnoreCase(JSONUtil.parseObj(result).get("Code").toString())||
               "AlreadyExists".equalsIgnoreCase(JSONUtil.parseObj(result).get("Code").toString())
        ;
    }


    /**
     * 检查WPS文件转换结果
     * 调用WPS格式转换查询接口
     * 可以在回调中执行，也可以用于异步查询结果
     * 返回转换后的文件地址，地址是有时限的，所以需要及时使用或保存，建议保存到自己服务器中，以免因过期问题打开地址失败
     * */
    default byte[] getConvertFile(String taskId) throws Exception{
        String headerDate = EasyPoiWpsUtil.getGMTDate() ;
        String downUrl = "" ;
        //请求参数
        String contentMd5 = EasyPoiWpsUtil.getMD5(null) ; //请求内容数据的MD5值，用null作入参
        String url = QUERY_API + "?TaskId=" + taskId + "&AppId=" + getAppId();
        String signature = EasyPoiWpsUtil.getSignature("GET" , url , contentMd5 , headerDate,"application/json",getAppSecret()) ;//签名url的参数带请求参数
        String authorization =  "WPS " + getAppId() + ":" + signature ;
        //header参数
        Map<String, String> headers = Maps.newLinkedHashMap();
        headers.put(HttpHeaders.CONTENT_TYPE ,"application/json" ) ;
        headers.put(HttpHeaders.DATE , headerDate) ;
        headers.put("Content-MD5" , contentMd5) ;//文档上是 "Content-Md5"
        headers.put(HttpHeaders.AUTHORIZATION , authorization) ;
        //调用
        String result = HttpUtil.createGet(url).addHeaders(headers).execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        String code =jsonObject.get("Code").toString() ;
        if (code.equals("OK")){
            if (jsonObject.get("Urls") != null){ //实际上返回这个参数
                downUrl = (jsonObject.getJSONArray("Urls").getStr(0)).toString() ;
            }
            else if (jsonObject.get("Url") != null){//文档是返回这个参数
                downUrl = jsonObject.get("Url").toString() ;
            }else {
                throw  new RuntimeException("未获取到下载文件地址");
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HttpUtil.download(downUrl,bos,true);
        return bos.toByteArray();
    }
}
