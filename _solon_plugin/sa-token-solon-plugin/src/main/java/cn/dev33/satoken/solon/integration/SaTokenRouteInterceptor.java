package cn.dev33.satoken.solon.integration;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.solon.model.SaRequestForSolon;
import cn.dev33.satoken.solon.model.SaResponseForSolon;
import cn.dev33.satoken.stp.StpUtil;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * sa-token基于路由的拦截式鉴权 
 * @author kong
 */
public class SaTokenRouteInterceptor implements Handler {

	/**
	 * 每次进入拦截器的[执行函数]，默认为登录校验
	 */
	protected SaRouteFunction function = (req, res, handler) -> StpUtil.checkLogin();

	/**
	 * 创建一个路由拦截器
	 */
	public SaTokenRouteInterceptor() {
	}

	/**
	 * 创建, 并指定[执行函数]
	 *
	 * @param function [执行函数]
	 */
	public SaTokenRouteInterceptor(SaRouteFunction function) {
		this.function = function;
	}

	/**
	 * 静态方法快速构建一个
	 *
	 * @param function 自定义模式下的执行函数
	 * @return sa路由拦截器
	 */
	public static SaTokenRouteInterceptor newInstance(SaRouteFunction function) {
		return new SaTokenRouteInterceptor(function);
	}


	@Override
	public void handle(Context ctx) throws Throwable {
		try {
			function.run(new SaRequestForSolon(), new SaResponseForSolon(), ctx);
		} catch (StopMatchException e) {
			// 停止匹配，进入Controller
		} catch (BackResultException e) {
			// 停止匹配，向前端输出结果
			ctx.output(e.getMessage());
			ctx.setHandled(true);
			ctx.setRendered(true);
			return;
		}
	}
}
