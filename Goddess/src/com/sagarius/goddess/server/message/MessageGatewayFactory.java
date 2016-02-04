package com.sagarius.goddess.server.message;

public class MessageGatewayFactory {
	public static MessageGateway getGateway() {
		return new AcharyaGateway();
	}
}
