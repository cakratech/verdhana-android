package com.bcasekuritas.rabbitmq;

public class MQGroup {

	private String prefix;
	private String outerClass;
	private String basePackage;
	private String baseMessage;
	private String enumType;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getOuterClass() {
		return outerClass;
	}

	public void setOuterClass(String outerObject) {
		this.outerClass = outerObject;
	}

	public String getBaseMessage() {
		return baseMessage;
	}

	public void setBaseMessage(String baseMessage) {
		this.baseMessage = baseMessage;
	}

	public String getEnumType() {
		return enumType;
	}

	public void setEnumType(String enumType) {
		this.enumType = enumType;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

}
