package com.bcasekuritas.rabbitmq.rpc;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.bcasekuritas.rabbitmq.rpc.method.DelegateMethod;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class MQServerRPC<T,P> {
    private String uri;
    private String user;
    private String pass;
    
    private Connection conn = null;
    private Channel ch = null;
    private String rpcName;
    
    private DelegateMethod<T, P> delegateMethod;
    
//    private static final Logger logger = LogManager.getLogger(MQServerRPC.class);
    
	public MQServerRPC(String uri, String user, String pass, String rpcName) throws Exception  {
		this.uri = uri;
		this.user = user;
		this.pass = pass;
		
		this.rpcName = rpcName;
		
		ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(this.uri);
        factory.setUsername(this.user);
		factory.setPassword(this.pass);
        
        conn = factory.newConnection();
		
//        registerMethod();
	}
	
	public MQServerRPC(Connection conn, String rpcName) {
		this.conn = conn;
		
		this.rpcName = rpcName;
	}
	
//	public void addDelegateMethod(BytesConverter<T, P> converter, DelegateMethod<T, P> method)
	public void addDelegateMethod(DelegateMethod<T, P> method)
	{
//		this.converter = converter;
		this.delegateMethod = method;
	
	}
		
	
	public void registerMethod() throws Exception
	{

        ch = conn.createChannel();
//        ch.basicQos(20);
        
        ch.queueDeclare(rpcName, false, true, false, null);
        
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-cancel-on-ha-failover", false);
        
//        ch.basicConsume(rpcName, true, args, new DefaultConsumer(ch) {
        ch.basicConsume(rpcName, false, args, new DefaultConsumer(ch) {        
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
//            	boolean hasPublish = false;
//            	String error = "";
            	
        	    BasicProperties replyProps = new BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
            	
/*            	if(delegateMethod!=null)
            	{
            	    try {
            	    	P result = delegateMethod.processMethod(delegateMethod.toObject(body));	
            		
						ch.basicPublish("", properties.getReplyTo(), replyProps, delegateMethod.toByteArray(result));
						
						hasPublish = true;
					} catch (Exception e) {
//						logger.error(e.getMessage());
						e.printStackTrace();
						
						error = e.getMessage();
					}
            	}
            	else
            	{
            		error = "Delegate Method is null";
            	}
            	
            	if(!hasPublish)
            	{
            		try {
						ch.basicPublish("", properties.getReplyTo(), replyProps, ("Error: " + error).getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}*/
        	    byte[] response = new byte[0];
        	    try {
        	    	if(delegateMethod!=null)
                	{
        	    		P result = delegateMethod.processMethod(delegateMethod.toObject(body));
        	    		
        	    		response = delegateMethod.toByteArray(result);
                	}
        	    	else
        	    	{
        	    		throw new RuntimeException("Delegate Method is null");
        	    	}
				} catch (RuntimeException e) {
					e.printStackTrace();
				} 
        	    finally {
        	    	ch.basicPublish("", properties.getReplyTo(), replyProps, response);
        	    	
        	    	ch.basicAck(envelope.getDeliveryTag(), false);
				}
                
            }
        });
	}
	
	
		
}
