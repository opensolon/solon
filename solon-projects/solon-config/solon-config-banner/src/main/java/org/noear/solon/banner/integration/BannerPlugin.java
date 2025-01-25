/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.banner.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ResourceUtil;

import java.io.IOException;

/**
 * @author pmg1991
 * @since 1.11
 * */
public class BannerPlugin implements Plugin {
	String BANNER_DEF_FILE = "META-INF/solon_def/banner-def.txt";

	public BannerPlugin() throws IOException {
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
	public void start(AppContext context) throws Throwable {

	}
}
