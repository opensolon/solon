package tech.smartboot.feat;


/**
 * Feat框架的核心工具类，提供HTTP服务器、文件服务器、HTTP客户端和WebSocket等功能的快速构建方法。
 * 该类采用流式API设计，支持链式调用，使用简单直观。主要功能包括：
 * <ul>
 *   <li>HTTP服务器：快速构建高性能HTTP服务器，对标vert.x</li>
 *   <li>文件服务器：静态资源服务器，支持反向代理，对标nginx</li>
 *   <li>HTTP客户端：支持RESTFUL API调用，包含JSON请求等常用场景</li>
 *   <li>WebSocket：提供WebSocket客户端功能</li>
 * </ul>
 *
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class Feat {
    /**
     * 当前 Feat 框架版本号
     */
    public static final String VERSION = "v2.3.1";
}
