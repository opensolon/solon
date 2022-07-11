package cn.dev33.satoken.solon.integration;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
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
	 * 异常处理函数：每次[认证函数]发生异常时执行此函数
	 */
	protected SaFilterErrorStrategy error = e -> {
		if (e instanceof SaTokenException) {
			throw (SaTokenException) e;
		} else {
			throw new SaTokenException(e);
		}
	};

	/**
	 * 写入[异常处理函数]：每次[认证函数]发生异常时执行此函数
	 *
	 * @param error see note
	 * @return 对象自身
	 */
	public SaTokenRouteInterceptor setError(SaFilterErrorStrategy error) {
		this.error = error;
		return this;
	}

	public SaTokenRouteInterceptor setFunction(SaRouteFunction function) {
		this.function = function;
		return this;
	}

	@Override
	public void handle(Context ctx) throws Throwable {
		try {
			function.run(new SaRequestForSolon(), new SaResponseForSolon(), ctx);
		} catch (StopMatchException e) {

		} catch (BackResultException e) {
			// 1. 获取异常处理策略结果
			Object result = error.run(e);

			// 2. 写入输出流
			if (result != null) {
				ctx.render(result);
				ctx.setHandled(true);
				return;
			} else {
				throw e;
			}
		}
	}
}
