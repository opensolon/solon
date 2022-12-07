package org.noear.solon.extend.banner;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        boolean enable = Solon.cfg().getBool("solon.banner.enable", true);
		String mode = Solon.cfg().get("solon.banner.mode", "console");

        if (enable) {
            
			switch(mode)
			{
				case "log": 
					LogUtil.global().info(SolonBannerPrinter.printBanner());
					break;
				case "both":
					LogUtil.global().info(SolonBannerPrinter.printBanner());
				case "console": 
				default:
					System.out.println(SolonBannerPrinter.printBanner());
					break;
					
			}
        }
    }
}
