package org.noear.solon.banner;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.bean.InitializingBean;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ResourceUtil;

/**
 * @author pmg1991
 * @since 1.11
 * */
public class XPluginImp implements Plugin, InitializingBean {
	String BANNER_DEF_FILE = "META-INF/solon_def/banner-def.txt";

	@Override
	public void afterInjection() throws Throwable {
		boolean enable = Solon.cfg().getBool("solon.banner.enable", true);

		if (enable) {
			String mode = Solon.cfg().get("solon.banner.mode", "console");
			String path = Solon.cfg().get("solon.banner.path", "banner.txt");

			String bannerTxt = ResourceUtil.getResourceAsString(path);
			if (Utils.isEmpty(bannerTxt)) {
				bannerTxt = ResourceUtil.getResourceAsString(BANNER_DEF_FILE);
			}

			//Trying to get the banner file Solon
			if (Utils.isEmpty(bannerTxt)) {
				return;
			}

			bannerTxt = bannerTxt.replace("${solon.version}", Solon.version());

			switch (mode) {
				case "log":
					LogUtil.global().info(bannerTxt);
					break;
				case "both":
					LogUtil.global().info(bannerTxt);
				case "console":
				default:
					System.out.println(bannerTxt);
					break;

			}
		}
	}

	@Override
	public void start(AopContext context) throws Throwable {

	}
}
