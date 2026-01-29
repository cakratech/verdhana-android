package com.bcasekuritas.rabbitmq.consumer;

import com.google.protobuf.Message;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MQConsumerFactory <P extends Message> {
    
    private String uri;
    private Connection conn = null;
//    private Channel ch = null;
    
    
    private final ConcurrentMap<String, MQConsumer<P>> consumers = new ConcurrentHashMap<String, MQConsumer<P>>();
    
	public MQConsumerFactory(String uri, String userName, String password) throws Exception {
		this.uri = uri;
		
		ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(this.uri);
        factory.setUsername(userName);
		factory.setPassword(password);
        
        this.conn = factory.newConnection();
        
	}
	
	public MQConsumerFactory(Connection conn) throws Exception {
		this.conn = conn;
	}
	
	public MQConsumer<P> getConsumerInstance(String name, String mode, Class<P> builderClass,
                                             boolean durable, boolean autoDelete) throws Exception
	{
		MQConsumer<P> ret;
		
		ret = consumers.get(name);
		
		if(ret == null)
		{
			ret = new MQConsumer<P>(name, mode, conn, builderClass, durable, autoDelete);
			
			consumers.put(name, ret);
		}
		
		return ret;
	}
	
	public MQConsumer<P> getConsumerInstance(String name, String mode, Class<P> builderClass) throws Exception
	{
		MQConsumer<P> ret;
		
		ret = consumers.get(name);
		
		if(ret == null)
		{
			ret = new MQConsumer<P>(name, mode, conn, builderClass, false, false);
			
			consumers.put(name, ret);
		}
		
		return ret;
	}
	
	/*public MQConsumer getConsumerInstance(Type type, String receiveType) throws Exception
	{
		return getConsumerInstance(generateExchangeName(type.toString(), receiveType));
	}*/
	
	/*static public String generateExchangeName(String msgType, String receiveType)
	{
		return (msgType + "-" + receiveType).toLowerCase();
	}
	
	static public String generateRoutingKey(String boardCode, String secCode)
	{
		return boardCode + "." + secCode;
	}*/
	
	/*public static String convertType(Type type)
	{
		String ret = "";
		switch (type) {
		case AVG_PRICE_INFO_LIST:
			ret = Constant.DFAvgPriceInfoListMessageType;
			break;
		case LATEST_PRICE:
			ret = Constant.DFLatestPriceMessageType;
			break;
		case ORDER_DATA:
			ret = Constant.DFOrderDataMessageType;
			break;
		case ORDER_INFO_LIST:
			ret = Constant.DFOrderInfoListMessageType;
			break;
		case ORDERBOOK_SUMMARY:
			ret = Constant.DFOrderBookSummaryDataMessageType;
			break;
		case TRADE_DATA:
			ret = Constant.DFTradeDataMessageType;
			break;
		case TRADE_DETAIL_DATA:
			ret = Constant.DFTradeDetailDataMessageType;
			break;
		case TRADE_SUMMARY:
			ret = Constant.DFTradeSummaryDataMessageType;
			break;	
		default:
			break;
		}
		
		return ret;
	}
	
	public static Type convertConstant(String type)
	{
		Type ret = null;
		switch (type) {
		case Constant.DFAvgPriceInfoListMessageType:
			ret = Type.AVG_PRICE_INFO_LIST;
			break;
		case Constant.DFLatestPriceMessageType:
			ret = Type.LATEST_PRICE;
			break;
		case Constant.DFOrderDataMessageType:
			ret = Type.ORDER_DATA;
			break;
		case Constant.DFOrderInfoListMessageType:
			ret = Type.ORDER_INFO_LIST;
			break;
		case Constant.DFOrderBookSummaryDataMessageType:
			ret = Type.ORDERBOOK_SUMMARY;
			break;
		case Constant.DFTradeDataMessageType:
			ret = Type.TRADE_DATA;
			break;
		case com.tech.cakra.datafeed.client.common.Constant.DFTradeDetailDataMessageType:
			ret = Type.TRADE_DETAIL_DATA;
			break;
		case Constant.DFTradeSummaryDataMessageType:
			ret = Type.TRADE_SUMMARY;
			break;	
		default:
			break;
		}
		
		return ret;
	}*/

}
