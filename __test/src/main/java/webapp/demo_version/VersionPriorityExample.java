package webapp.demo_version;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 版本优先级示例
 * 
 * 演示当多个路由同时匹配时，如何选择最高版本的处理器
 */
@Controller
public class VersionPriorityExample {

    /**
     * 用户 API v1.0+ - 模式匹配
     * 匹配 1.0, 1.1, 1.2... 等版本
     */
    @Mapping(value = "/api/users", version = "1.0+")
    public String getUsersV1Plus() {
        return "用户列表 - API v1.0+（增强版）";
    }

    /**
     * 用户 API v2.0 - 精确匹配
     * 匹配 2.0 版本
     */
    @Mapping(value = "/api/users", version = "2.0")
    public String getUsersV2() {
        return "用户列表 - API v2.0（重大更新）";
    }

    /**
     * 用户 API v2.1+ - 模式匹配
     * 匹配 2.1, 2.2... 等版本
     */
    @Mapping(value = "/api/users", version = "2.1+")
    public String getUsersV2Plus() {
        return "用户列表 - API v2.1+（最新版）";
    }

    /**
     * 订单 API v1.0 - 精确匹配
     */
    @Mapping(value = "/api/orders", version = "1.0")
    public String getOrdersV1() {
        return "订单列表 - API v1.0（基础版）";
    }

    /**
     * 订单 API v1.0+ - 模式匹配
     */
    @Mapping(value = "/api/orders", version = "1.0+")
    public String getOrdersV1Plus() {
        return "订单列表 - API v1.0+（增强版）";
    }

    /**
     * 订单 API v1.1 - 精确匹配
     */
    @Mapping(value = "/api/orders", version = "1.1")
    public String getOrdersV11() {
        return "订单列表 - API v1.1（中等更新）";
    }

    /**
     * 产品 API v1.0+ - 模式匹配
     */
    @Mapping(value = "/api/products", version = "1.0+")
    public String getProductsV1Plus() {
        return "产品列表 - API v1.0+（稳定版）";
    }

    /**
     * 产品 API v2.0+ - 模式匹配
     */
    @Mapping(value = "/api/products", version = "2.0+")
    public String getProductsV2Plus() {
        return "产品列表 - API v2.0+（下一代版）";
    }
}

/**
 * 版本优先级规则说明：
 * 
 * 1. 精确匹配优先于模式匹配
 *    - 请求版本 2.0 时，同时匹配 "1.0+" 和 "2.0"，优先选择 "2.0"
 * 
 * 2. 高版本优先于低版本
 *    - 同时匹配 "1.0+" 和 "1.1+" 时，优先选择 "1.1+"
 *    - 同时匹配 "2.0+" 和 "1.0+" 时，优先选择 "2.0+"
 * 
 * 3. 版本号比较规则：
 *    - 使用语义化版本：2.1.0 > 2.0.0 > 1.9.9 > 1.0.0
 *    - 比较每个版本号部分：主版本 > 次版本 > 修订版本
 * 
 * 4. 有版本优先于无版本
 *    - 同时存在有版本和无版本的路由时，优先选择有版本的
 * 
 * 匹配示例（假设客户端请求）：
 * 
 * GET /api/users (版本: 2.0)
 * -> 匹配：getUsersV1Plus(1.0+), getUsersV2(2.0)
 * -> 选择：getUsersV2(2.0) [精确匹配优先]
 * 
 * GET /api/users (版本: 2.2)
 * -> 匹配：getUsersV1Plus(1.0+), getUsersV2Plus(2.1+)
 * -> 选择：getUsersV2Plus(2.1+) [高版本优先]
 * 
 * GET /api/orders (版本: 1.0)
 * -> 匹配：getOrdersV1(1.0), getOrdersV1Plus(1.0+)
 * -> 选择：getOrdersV1(1.0) [精确匹配优先]
 * 
 * GET /api/orders (版本: 1.1)
 * -> 匹配：getOrdersV1Plus(1.0+), getOrdersV11(1.1)
 * -> 选择：getOrdersV11(1.1) [精确匹配优先]
 * 
 * GET /api/products (版本: 2.1)
 * -> 匹配：getProductsV1Plus(1.0+), getProductsV2Plus(2.0+)
 * -> 选择：getProductsV2Plus(2.0+) [高版本优先]
 * 
 * 这种优先级设计确保了：
 * - 客户端总是能获得最适合当前版本的API响应
 * - 新版本的API改进能够自动被兼容的客户端访问
 * - 精确的版本匹配不会被模糊的模式匹配意外覆盖
 */