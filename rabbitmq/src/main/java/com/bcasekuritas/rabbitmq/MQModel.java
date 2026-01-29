package com.bcasekuritas.rabbitmq;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.bcasekuritas.rabbitmq.common.Constant.ConfigType;
import com.bcasekuritas.rabbitmq.common.Constant.MQMode;
import com.bcasekuritas.rabbitmq.pool.ChannelPooled;

public class MQModel {
    private MQGroup mqGroup;
    private String exchangeName;
    private String queueName;
    private String msgObj;
    private String msgType;
    private String recvType;
    private String routingCriteria;
    private String flowModel;

    private Class<?> baseClazz;
    private Class<?> typeClazz;
    private Class<?> msgClazz;

    private boolean durable = false;
    private boolean autoDelete = false;
    private boolean persistence = false;


    private MQMode mqMode = MQMode.TOPIC; //default mode

    private ConfigType configType = ConfigType.SIMPLE;

    private com.rabbitmq.client.Channel channel;

    public ChannelPooled getChannelPooled() {
        return channelPooled;
    }

    private ChannelPooled channelPooled;


    public MQModel(String name, String flowModel, com.rabbitmq.client.Channel channel) throws Exception {
        this(name, null, null, null, null, null, flowModel, false, false, false, channel);
    }

    public MQModel(String name, String flowModel, ChannelPooled channelPooled) throws Exception {
        super();

        this.flowModel = flowModel;
        this.channelPooled = channelPooled;

        if (flowModel != null && !flowModel.isEmpty()) {
            if (this.flowModel.equalsIgnoreCase("simple")) {
                this.mqMode = MQMode.SIMPLE;
            } else if (this.flowModel.equalsIgnoreCase("fanout")) {
                this.mqMode = MQMode.FANOUT;
            } else if (this.flowModel.equalsIgnoreCase("direct")) {
                this.mqMode = MQMode.DIRECT;
            } else if (this.flowModel.equalsIgnoreCase("topic")) {
                this.mqMode = MQMode.TOPIC;
            } else {
                throw new Exception("Invalid flow model value: " + flowModel);
            }
        }

//		if(name!=null && !name.isEmpty())
        if (mqGroup == null) {
            //Simple exchange config
            this.configType = ConfigType.SIMPLE;
        } else {
            this.configType = ConfigType.PROTOBUF;
        }

        if (this.mqGroup != null) {
            //Protobuf exchange config
            if (this.mqGroup.getBasePackage() != null && !this.mqGroup.getBasePackage().isEmpty()) //protobuf generated with option multiple files on
            {
                baseClazz = Class.forName(this.mqGroup.getBasePackage() + "." + this.mqGroup.getBaseMessage());

                if (this.mqGroup.getEnumType().indexOf(".") >= 0) {
                    typeClazz = Class.forName(this.mqGroup.getEnumType());
                } else {
                    typeClazz = Class.forName(this.mqGroup.getBasePackage() + "." + this.mqGroup.getBaseMessage() + "$" + this.mqGroup.getEnumType());
                }


                msgClazz = Class.forName(this.mqGroup.getBasePackage() + "." + this.msgObj);
            } else //outer class =>  //protobuf generated with option multiple files off
            {
                baseClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.mqGroup.getBaseMessage());

                if (this.mqGroup.getEnumType().indexOf(".") >= 0) {
                    typeClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.mqGroup.getEnumType());
                } else {
                    typeClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.mqGroup.getBaseMessage() + "$" + this.mqGroup.getEnumType());
                }


                msgClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.msgObj);
            }

        }

//		if(this.configType == ConfigType.SIMPLE)
        if (name != null && !name.isEmpty()) {
            if (this.mqMode != MQMode.SIMPLE) {
                this.exchangeName = name;
            } else {
                this.queueName = name;
                this.exchangeName = "";
            }
        } else {
            String prefix = (this.mqGroup != null && !this.mqGroup.getPrefix().isEmpty()) ? this.mqGroup.getPrefix() : "";

            if (this.mqMode != MQMode.SIMPLE) {
//				if(exchangeName==null || exchangeName.isEmpty())
//				{
                if (this.recvType != null && !this.recvType.isEmpty()) {
                    this.exchangeName = (prefix + "." + this.msgType + "-" + this.recvType).toLowerCase();
                } else {
                    this.exchangeName = (prefix + "." + this.msgType).toLowerCase();
                }

                this.queueName = "";

//				}
            } else {
//				if(queueName==null || queueName.isEmpty())
//				{
                if (this.recvType != null && !this.recvType.isEmpty()) {
                    this.queueName = (prefix + "." + this.msgType + "-" + this.recvType).toLowerCase();
                } else {
                    this.queueName = (prefix + "." + this.msgType).toLowerCase();
                }

                this.exchangeName = "";

//				}
            }
        }
    }

    public MQModel(String name, String flowModel) throws Exception {
        this(name, null, null, null, null, null, flowModel, false, false, false);
    }

    public MQModel(String name, String flowModel, boolean durable, boolean autoDelete, boolean persistence) throws Exception {
        this(name, null, null, null, null, null, flowModel, durable, autoDelete, persistence);
    }

    public MQModel(MQGroup mqGroup, String msgObj, String msgType,
                   String recvType, String routingCriteria, String flowModel, boolean durable, boolean autoDelete, boolean persistence) throws Exception {
        this(null, mqGroup, msgObj, msgType, recvType, routingCriteria, flowModel, durable, autoDelete, persistence);
    }

    public MQModel(String name, MQGroup mqGroup, String msgObj, String msgType,
                   String recvType, String routingCriteria, String flowModel, boolean durable, boolean autoDelete, boolean persistence) throws Exception {
        this(name, mqGroup, msgObj, msgType, recvType, routingCriteria, flowModel, durable, autoDelete, persistence, null);
    }

    /**
     * @param name
     * @param msgType
     * @param recvType
     * @param flowModel
     * @throws Exception
     */
    public MQModel(String name, MQGroup mqGroup, String msgObj, String msgType,
                   String recvType, String routingCriteria, String flowModel, boolean durable, boolean autoDelete, boolean persistence,
                   com.rabbitmq.client.Channel channel) throws Exception {
        super();

        this.mqGroup = mqGroup;
        this.msgObj = msgObj;
        this.msgType = msgType;
        this.recvType = recvType;
        this.routingCriteria = routingCriteria;
        this.flowModel = flowModel;
        this.durable = durable;
        this.autoDelete = autoDelete;
        this.persistence = persistence;
        this.channel = channel;

        if (flowModel != null && !flowModel.isEmpty()) {
            if (this.flowModel.equalsIgnoreCase("simple")) {
                this.mqMode = MQMode.SIMPLE;
            } else if (this.flowModel.equalsIgnoreCase("fanout")) {
                this.mqMode = MQMode.FANOUT;
            } else if (this.flowModel.equalsIgnoreCase("direct")) {
                this.mqMode = MQMode.DIRECT;
            } else if (this.flowModel.equalsIgnoreCase("topic")) {
                this.mqMode = MQMode.TOPIC;
            } else {
                throw new Exception("Invalid flow model value: " + flowModel);
            }
        }

//		if(name!=null && !name.isEmpty())
        if (mqGroup == null) {
            //Simple exchange config
            this.configType = ConfigType.SIMPLE;
        } else {
            this.configType = ConfigType.PROTOBUF;
        }

        if (this.mqGroup != null) {
            //Protobuf exchange config
            if (this.mqGroup.getBasePackage() != null && !this.mqGroup.getBasePackage().isEmpty()) //protobuf generated with option multiple files on
            {
                baseClazz = Class.forName(this.mqGroup.getBasePackage() + "." + this.mqGroup.getBaseMessage());

                if (this.mqGroup.getEnumType().indexOf(".") >= 0) {
                    typeClazz = Class.forName(this.mqGroup.getEnumType());
                } else {
                    typeClazz = Class.forName(this.mqGroup.getBasePackage() + "." + this.mqGroup.getBaseMessage() + "$" + this.mqGroup.getEnumType());
                }


                msgClazz = Class.forName(this.mqGroup.getBasePackage() + "." + this.msgObj);
            } else //outer class =>  //protobuf generated with option multiple files off
            {
                baseClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.mqGroup.getBaseMessage());

                if (this.mqGroup.getEnumType().indexOf(".") >= 0) {
                    typeClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.mqGroup.getEnumType());
                } else {
                    typeClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.mqGroup.getBaseMessage() + "$" + this.mqGroup.getEnumType());
                }


                msgClazz = Class.forName(this.mqGroup.getOuterClass() + "$" + this.msgObj);
            }

        }

//		if(this.configType == ConfigType.SIMPLE)
        if (name != null && !name.isEmpty()) {
            if (this.mqMode != MQMode.SIMPLE) {
                this.exchangeName = name;
            } else {
                this.queueName = name;
                this.exchangeName = "";
            }
        } else {
            String prefix = (this.mqGroup != null && !this.mqGroup.getPrefix().isEmpty()) ? this.mqGroup.getPrefix() : "";

            if (this.mqMode != MQMode.SIMPLE) {
//				if(exchangeName==null || exchangeName.isEmpty())
//				{
                if (this.recvType != null && !this.recvType.isEmpty()) {
                    this.exchangeName = (prefix + "." + this.msgType + "-" + this.recvType).toLowerCase();
                } else {
                    this.exchangeName = (prefix + "." + this.msgType).toLowerCase();
                }

                this.queueName = "";

//				}
            } else {
//				if(queueName==null || queueName.isEmpty())
//				{
                if (this.recvType != null && !this.recvType.isEmpty()) {
                    this.queueName = (prefix + "." + this.msgType + "-" + this.recvType).toLowerCase();
                } else {
                    this.queueName = (prefix + "." + this.msgType).toLowerCase();
                }

                this.exchangeName = "";

//				}
            }
        }

    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getFlowModel() {
        return flowModel;
    }

    public void setFlowModel(String flowModel) {
        this.flowModel = flowModel;
    }

    public String getRoutingCriteria() {
        return routingCriteria;
    }

    public void setRoutingCriteria(String routingCriteria) {
        this.routingCriteria = routingCriteria;
    }

    public MQMode getMqMode() {
        return mqMode;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getRecvType() {
        return recvType;
    }

    public void setRecvType(String recvType) {
        this.recvType = recvType;
    }

    public String getKey() {
        return (this.mqMode == MQMode.SIMPLE ? this.queueName : this.exchangeName);
    }

    public Class<?> getBaseClazz() {
        return baseClazz;
    }

    public Class<?> getTypeClazz() {
        return typeClazz;
    }

    public Class<?> getMsgClazz() {
        return msgClazz;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public String generateRoutingKey(com.google.protobuf.Message msg) throws Exception {
        String result = routingCriteria;

        String[] fields = result.split("\\.");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];

            Method method = msg.getClass().getMethod("get" + Character.toUpperCase(field.charAt(0)) +
                    field.substring(1));

            String value = method.invoke(msg).toString();
//			System.out.println("value: " + value);
            result = result.replaceFirst(field, value);

        }

        return result;

    }

    public com.google.protobuf.Message wrapperMessage(com.google.protobuf.Message msg) throws Exception {
        return wrapperMessage(msg, "");
    }

    public com.google.protobuf.Message wrapperMessage(com.google.protobuf.Message msg, String sessionId, List<String> deviceIds) throws Exception {

        return wrapperMessage(msg, sessionId, deviceIds, null);
		
		/*com.google.protobuf.Message retMsg;
		
		//2) Wrapping to baseObject
		Method method = baseClazz.getMethod("newBuilder");
		Object builder = method.invoke(null);
		
		//2.a) Set type to mappingType
		method = typeClazz.getMethod("valueOf", String.class);
		Object typeVal = method.invoke(null, msgType);
		
		method = builder.getClass().getMethod("setType", typeClazz);
		method.invoke(builder, typeVal);
			
		//2.b) Set property mappingObject
		method = builder.getClass().getMethod("set" + msgObj, msgClazz);
		method.invoke(builder, msg);
		
		//2.c) Set sending time
		method = builder.getClass().getMethod("setSendingTime", long.class);
		method.invoke(builder, System.currentTimeMillis());
		
		//2.d) Set session id if no empty/null
		if(sessionId!=null && !sessionId.isEmpty())
		{
			method = builder.getClass().getMethod("setSessionId", String.class);
			method.invoke(builder, sessionId);	
		}
		
		//2.e) Set device if no empty/null
		if(deviceIds!=null && !deviceIds.isEmpty())
		{
			method = builder.getClass().getMethod("addAllDeviceId", Iterable.class);
			method.invoke(builder, deviceIds);	
		}
		
		
		//2.f) build object
		method = builder.getClass().getMethod("build");
		retMsg = (com.google.protobuf.Message)method.invoke(builder);
		
		return retMsg;*/
    }

    public com.google.protobuf.Message wrapperMessage(com.google.protobuf.Message msg, String sessionId, List<String> deviceIds, Map<String, Object> datas) throws Exception {
        com.google.protobuf.Message retMsg;

        //2) Wrapping to baseObject
        Method method = baseClazz.getMethod("newBuilder");
        Object builder = method.invoke(null);

        //2.a) Set type to mappingType
        method = typeClazz.getMethod("valueOf", String.class);
        Object typeVal = method.invoke(null, msgType);

        method = builder.getClass().getMethod("setType", typeClazz);
        method.invoke(builder, typeVal);

        //2.b) Set property mappingObject
        method = builder.getClass().getMethod("set" + msgObj, msgClazz);
        method.invoke(builder, msg);

        //2.c) Set sending time
        method = builder.getClass().getMethod("setSendingTime", long.class);
        method.invoke(builder, System.currentTimeMillis());

        //2.d) Set session id if no empty/null
        if (sessionId != null && !sessionId.isEmpty()) {
            method = builder.getClass().getMethod("setSessionId", String.class);
            method.invoke(builder, sessionId);
        }

        //2.e) Set device if no empty/null
        if (deviceIds != null && !deviceIds.isEmpty()) {
            method = builder.getClass().getMethod("addAllDeviceId", Iterable.class);
            method.invoke(builder, deviceIds);
        }

        if (datas != null) {
            for (Map.Entry<String, Object> entry : datas.entrySet()) {

                if (entry.getValue() instanceof Long) {
//					long val = (Long)entry.getValue(); 
                    method = builder.getClass().getMethod(entry.getKey(), long.class);
                    method.invoke(builder, entry.getValue());
                } else if (entry.getValue() instanceof Integer) {
//					long val = (Long)entry.getValue(); 
                    method = builder.getClass().getMethod(entry.getKey(), int.class);
                    method.invoke(builder, entry.getValue());
                } else if (entry.getValue() instanceof Double) {
//					long val = (Long)entry.getValue(); 
                    method = builder.getClass().getMethod(entry.getKey(), double.class);
                    method.invoke(builder, entry.getValue());
                } else if (entry.getValue() instanceof Float) {
//					long val = (Long)entry.getValue(); 
                    method = builder.getClass().getMethod(entry.getKey(), float.class);
                    method.invoke(builder, entry.getValue());
                } else if (entry.getValue() instanceof Boolean) {
//					long val = (Long)entry.getValue(); 
                    method = builder.getClass().getMethod(entry.getKey(), boolean.class);
                    method.invoke(builder, entry.getValue());
                } else {
                    method = builder.getClass().getMethod(entry.getKey(), entry.getValue().getClass());
                    method.invoke(builder, entry.getValue());
                }


            }
        }


        //2.f) build object
        method = builder.getClass().getMethod("build");
        retMsg = (com.google.protobuf.Message) method.invoke(builder);

        return retMsg;
    }

    public com.google.protobuf.Message wrapperMessage(com.google.protobuf.Message msg, Map<String, Object> datas) throws Exception {
        return wrapperMessage(msg, null, null, datas);
    }

    public com.google.protobuf.Message wrapperMessage(com.google.protobuf.Message msg, String sessionId) throws Exception {
        return wrapperMessage(msg, sessionId, null);
		
		/*com.google.protobuf.Message retMsg;
		
		//2) Wrapping to baseObject
		Method method = baseClazz.getMethod("newBuilder");
		Object builder = method.invoke(null);
		
		//2.a) Set type to mappingType
		method = typeClazz.getMethod("valueOf", String.class);
		Object typeVal = method.invoke(null, msgType);
		
		method = builder.getClass().getMethod("setType", typeClazz);
		method.invoke(builder, typeVal);
			
		//2.b) Set property mappingObject
		method = builder.getClass().getMethod("set" + msgObj, msgClazz);
		method.invoke(builder, msg);
		
		//2.c) Set sending time
		method = builder.getClass().getMethod("setSendingTime", long.class);
		method.invoke(builder, System.currentTimeMillis());
		
		//2.d) Set session id if no empty/null
		if(sessionId!=null && !sessionId.isEmpty())
		{
			method = builder.getClass().getMethod("setSessionId", String.class);
			method.invoke(builder, sessionId);	
		}
		
		
		//2.e) build object
		method = builder.getClass().getMethod("build");
		retMsg = (com.google.protobuf.Message)method.invoke(builder);
		
		return retMsg;*/
    }


/*	public MQModel(String exchName)
	{
		this.exchangeName = exchName;
	}
	
	public MQModel(String msgType, String recvType)
	{
		this.exchangeName = (msgType + "-" + recvType).toLowerCase();
	}
	
	public String getExchangeName()
	{
		return this.exchangeName;
	}*/

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    public com.rabbitmq.client.Channel getChannel() {
        return channel;
    }

    public void setChannel(com.rabbitmq.client.Channel channel) {
        this.channel = channel;
    }

}