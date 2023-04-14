package demo1;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;

import demo.DemoApp;

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(DemoApp.class)
public class DemoTest1 {

	@Inject
	private Mailer mailer;
	
	@Test
	public void test() {
		//参考官网：https://www.simplejavamail.org/
		Set<String> tos = new HashSet<>();//收件人清单
		Set<String> ccs = new HashSet<>();//抄送人清单
		Set<String> bccs = new HashSet<>();//密送人清单
		String subject="邮件主题";
		String content ="邮件正文";
		Email mail = EmailBuilder.startingBlank()
				.toMultiple(tos)
				.ccAddresses(ccs)
				.bccAddresses(bccs)
				.withSubject(subject)
				.appendText(content)
				.buildEmail();
		mailer.sendMail(mail);//同步发送邮件
		mailer.sendMail(mail, true);//异步发送邮件
		System.out.println(mailer);
	}
}
