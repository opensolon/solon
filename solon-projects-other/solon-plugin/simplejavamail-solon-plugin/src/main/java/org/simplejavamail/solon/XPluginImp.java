package org.simplejavamail.solon;

import java.util.Properties;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.config.ConfigLoader;
import org.simplejavamail.mailer.MailerBuilder;



public class XPluginImp implements Plugin {

	@Override
	public void start(AppContext context) throws Throwable {
		String prefix = "simplejavamail";
		Properties props = context.cfg().getProp(prefix);
		Properties smprops = new Properties();
		props.forEach((k,v)->{
			smprops.put(prefix+"."+k, v);
		});
		ConfigLoader.loadProperties(smprops, true);
//		System.setProperty("mail.mime.splitlongparameters", "false");
		Mailer mailer = MailerBuilder.buildMailer();
		context.wrapAndPut(Mailer.class, mailer);
	}
}
