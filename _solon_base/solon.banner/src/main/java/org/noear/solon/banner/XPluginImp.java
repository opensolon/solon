package org.noear.solon.banner;

import java.io.IOException;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;

/**
 * @author pmg1991
 * @since 1.11
 * */
public class XPluginImp implements Plugin {
	@Override
	public void start(AopContext context) throws Throwable {
		boolean enable = Solon.cfg().getBool("solon.banner.enable", true);

		if (enable) {
			String mode = Solon.cfg().get("solon.banner.mode", "console");
			String path = Solon.cfg().get("solon.banner.path", "");

			String bannerTxt = "";
			if (Utils.isNotEmpty(path)) {
				try {
					bannerTxt = Utils.getResourceAsString(path);
				} catch (IOException e) {
					throw e;
				}
			}

			//Trying to get the banner file Solon
			if (Utils.isEmpty(bannerTxt)) {
				bannerTxt = SolonBannerPrinter.printBanner();
			}

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
}
