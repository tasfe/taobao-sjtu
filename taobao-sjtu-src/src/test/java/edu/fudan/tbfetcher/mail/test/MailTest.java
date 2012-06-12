package edu.fudan.tbfetcher.mail.test;

import org.junit.Test;

import edu.fudan.tbfetcher.mail.MailUtil;

public class MailTest {

	@Test
	public void testMail(){
		MailUtil util = new MailUtil();
		util.sendMail();
	}
}
