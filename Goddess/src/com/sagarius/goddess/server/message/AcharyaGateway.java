package com.sagarius.goddess.server.message;

public class AcharyaGateway extends SMSGateway {
	public AcharyaGateway() {
		smsRequestString = "http://www.admin.smsachariya.com/pushsms.php?";
		dlrString = "http://www.admin.smsachariya.com/fetchdlr.php?msgid=";
	}
}
