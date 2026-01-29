package com.bcasekuritas.rabbitmq.publisher;

import android.util.Log;

import com.bcasekuritas.rabbitmq.pool.ChannelPooled;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bcasekuritas.rabbitmq.MQModel;

import com.bcasekuritas.rabbitmq.common.Constant.ConfigType;
import com.bcasekuritas.rabbitmq.common.Constant.MQMode;

/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/

public class MQPublisher {
    private String uri;
    private String user;
    private String pass;

    private Channel channel;
    private boolean done = false;
    private Map<String, MQModel> mqModels = new HashMap<String, MQModel>();

    private ExecutorService executorService;

    public MQPublisher(String uri, String user, String pass) throws Exception {
        this.uri = uri;
        this.user = user;
        this.pass = pass;

        initConn();
    }

    public MQPublisher() {
//		this.channel = channel;
    }

    public void addMQModel(MQModel mqModel) {
//		mqModels.add(mqModel);
//		mqModels.put(mqModel.getKey(), mqModel);

		/*if(mqModel.getConfigType() == ConfigType.PROTOBUF)
		{
//			msgMap.put(mqModel.getKey(), mqModel.getMsgClazz().getName());
			msgMap.put(mqModel.getMsgClazz().getName(), mqModel.getKey());
		}*/

        if (mqModel.getConfigType() == ConfigType.PROTOBUF) {
            mqModels.put(mqModel.getMsgClazz().getName(), mqModel);
        } else {
            mqModels.put(mqModel.getKey(), mqModel);
        }

    }

    public MQModel getMQModel(String key) {
        return mqModels.get(key);
    }

    public MQModel getMQModelByMsgClassName(String clazzName) {
//		String key = (String)msgMap.inverseBidiMap().get(clazzName);

        return getMQModel(clazzName);
    }

    public void addMQModels(Collection<MQModel> mqModels) {
//		mqModels.addAll(mqModel);
        for (MQModel mqModel : mqModels) {
            addMQModel(mqModel);
        }
    }


    private void initConn() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        factory.setUsername(user);
        factory.setPassword(pass);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void registerMQModel() throws Exception {
        for (Map.Entry<String, MQModel> entry : mqModels.entrySet()) {
            if (entry.getValue().getMqMode() == MQMode.SIMPLE) {
                getChannel(entry.getKey()).queueDeclare(entry.getValue().getQueueName(), entry.getValue().isDurable(), false, entry.getValue().isAutoDelete(), null);
            } else {
                getChannel(entry.getKey()).exchangeDeclare(entry.getValue().getExchangeName(), entry.getValue().getFlowModel(),
                        entry.getValue().isDurable(), entry.getValue().isAutoDelete(), null);
            }
        }



		/*for (Iterator<MQModel> iterator = mqModels.iterator(); iterator.hasNext();) {
			MQModel mqModel = (MQModel) iterator.next();

			if(mqModel.getMqMode() == MQMode.SIMPLE)
			{
				channel.queueDeclare(mqModel.getQueueName(), false, false, false, null);
			}
			else
			{
				channel.exchangeDeclare(mqModel.getExchangeName(), mqModel.getFlowModel());
			}


		}*/
    }

    public boolean startPublisher() {
        boolean ret = true;

        try {
//				publishTask = new PublishTask(channel);

            executorService = Executors.newSingleThreadExecutor();
//	            executorService.execute(publishTask);
        } catch (Exception e) {
            ret = false;

            e.printStackTrace();

//	            logger.error("Failed to execute Job.", e);
//	            throw new RuntimeException("Failed to execute Job.", e);

        }

        return ret;

    }

    public boolean stopPublisher() {
        boolean ret = true;

        try {
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
//			logger.error("Error shutdown executorService: " + e.getMessage());

            ret = false;
        }


        return ret;
    }

    //binary message
    public void publishMessage(String exchangeName, String routingKey, AMQP.BasicProperties prop, byte[] data) throws Exception {
//		publishTask.putJob(exchangeName, routingKey, data);
//		executorService.execute(new PublishTask(exchangeName, routingKey, prop, data, channel));
        executorService.execute(new PublishTask(exchangeName, routingKey, prop, data, getChannel(exchangeName)));
    }

    public void publishMessage(String exchangeName, String routingKey, AMQP.BasicProperties prop, byte[] data, Channel channel) throws Exception {
        if (!channel.isOpen()) {
            throw new Exception("consume Listener: Ch close.");
        }
        executorService.execute(new PublishTask(exchangeName, routingKey, prop, data, channel));
    }

    public void publishMessageP(String exchangeName, String routingKey, AMQP.BasicProperties prop, byte[] data, ChannelPooled channelpooled) throws Exception {
        executorService.execute(new PublishTaskP(exchangeName, routingKey, prop, data, channelpooled));
    }

    private Channel getChannel(String exchangeName) {
        Channel ret = this.channel;

        MQModel mqModel = getMQModel(exchangeName);
        if (mqModel != null) {
            if (mqModel.getChannel() != null)
                ret = mqModel.getChannel();
        }

        return ret;
    }

    //binary protobuf message
    public void publishMessage(String exchangeName, String routingKey, AMQP.BasicProperties prop, com.google.protobuf.Message protobufMsg) throws Exception {
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        protobufMsg.writeTo(oStream);

        publishMessage(exchangeName, routingKey, prop, oStream.toByteArray());
    }

    //binary protobuf message
    public void publishMessage(String exchangeName, String routingKey, com.google.protobuf.Message protobufMsg) throws Exception {
        AMQP.BasicProperties prop = MessageProperties.MINIMAL_BASIC;

        String key = (exchangeName != null && !exchangeName.isEmpty()) ? exchangeName : routingKey;
        MQModel mqModel = getMQModel(key);
        if (mqModel != null) {
            // Get routing key
            if (routingKey == null || routingKey.isEmpty()) {
                if (mqModel.getMqMode() == MQMode.SIMPLE) {
                    routingKey = mqModel.getQueueName();
                } else if (mqModel.getMqMode() == MQMode.FANOUT) {
                    routingKey = "";
                } else {
                    throw new Exception("Routing key curently not implemented for MQMode => Topic and Direct");
                }
            }

            if (mqModel.isPersistence()) {
                prop = MessageProperties.PERSISTENT_BASIC;
            } else {
                prop = MessageProperties.BASIC;
            }
        }

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        protobufMsg.writeTo(oStream);
        Log.d("channelMq", "Channel Number publish " + String.valueOf(mqModel.getChannel().getChannelNumber()) + "  " + routingKey);
        publishMessage(exchangeName, routingKey, prop, oStream.toByteArray(), mqModel.getChannel() != null ? mqModel.getChannel() : this.channel);
    }

    //binary protobuf message
    public void publishMessageP(String exchangeName, String routingKey, com.google.protobuf.Message protobufMsg) throws Exception {
        AMQP.BasicProperties prop = MessageProperties.MINIMAL_BASIC;

        String key = (exchangeName != null && !exchangeName.isEmpty()) ? exchangeName : routingKey;
        MQModel mqModel = getMQModel(key);
        if (mqModel != null) {
            // Get routing key
            if (routingKey == null || routingKey.isEmpty()) {
                if (mqModel.getMqMode() == MQMode.SIMPLE) {
                    routingKey = mqModel.getQueueName();
                } else if (mqModel.getMqMode() == MQMode.FANOUT) {
                    routingKey = "";
                } else {
                    throw new Exception("Routing key curently not implemented for MQMode => Topic and Direct");
                }
            }

            if (mqModel.isPersistence()) {
                prop = MessageProperties.PERSISTENT_BASIC;
            } else {
                prop = MessageProperties.BASIC;
            }
        }

        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        protobufMsg.writeTo(oStream);

        assert mqModel != null;
        publishMessageP(exchangeName, routingKey, prop, oStream.toByteArray(), mqModel.getChannelPooled());
    }

    //binary protobuf message wrapper
    public void publishMessage(com.google.protobuf.Message protobufMsg) throws Exception {
        publishMessage(protobufMsg, "");

    }

    //binary protobuf message wrapper
    public void publishMessage(com.google.protobuf.Message protobufMsg, String routingKey, Map<String, Object> datas) throws Exception {
        publishMessage(protobufMsg, "", routingKey, null, datas);

    }

    //binary protobuf message wrapper
    public void publishMessage(com.google.protobuf.Message protobufMsg, Map<String, Object> datas) throws Exception {
        publishMessage(protobufMsg, "", "", null, datas);

    }

    //binary protobuf message wrapper
    public void publishMessage(com.google.protobuf.Message protobufMsg, String sessionId) throws Exception {
        publishMessage(protobufMsg, sessionId, "");

		/*MQModel mqModel = getMQModelByMsgClassName(protobufMsg.getClass().getName());

		if(mqModel!=null)
		{
			//Get routing key
			String routingKey = "";
			if (mqModel.getMqMode() == MQMode.SIMPLE)
			{
				routingKey = mqModel.getQueueName();
			}
			else if (mqModel.getMqMode() == MQMode.FANOUT)
			{
				routingKey = "";
			}
			else
			{
				routingKey = mqModel.generateRoutingKey(protobufMsg);
			}

			//Wrapper to base message
			com.google.protobuf.Message wrapperMsg = mqModel.wrapperMessage(protobufMsg, sessionId);
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			wrapperMsg.writeTo(oStream);

			publishMessage(mqModel.getExchangeName(), routingKey, oStream.toByteArray());
		}
		else
		{
			throw new Exception("MQModel for " + protobufMsg.getClass().getName() + " not found!");
		}*/

    }

    //binary protobuf message wrapper
    public void publishMessage(com.google.protobuf.Message protobufMsg, String sessionId, String routingKey, List<String> deviceIds, Map<String, Object> datas) throws Exception {
        MQModel mqModel = getMQModelByMsgClassName(protobufMsg.getClass()
                .getName());

        if (mqModel != null) {
            // Get routing key
            if (routingKey == null || routingKey.isEmpty()) {
                if (mqModel.getMqMode() == MQMode.SIMPLE) {
                    routingKey = mqModel.getQueueName();
                } else if (mqModel.getMqMode() == MQMode.FANOUT) {
                    routingKey = "";
                } else {
                    routingKey = mqModel.generateRoutingKey(protobufMsg);
                }
            }

            // Wrapper to base message
            com.google.protobuf.Message wrapperMsg = mqModel.wrapperMessage(
                    protobufMsg, sessionId, deviceIds, datas);
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            wrapperMsg.writeTo(oStream);

            if (mqModel.isPersistence()) {
                publishMessage(mqModel.getExchangeName(), routingKey, MessageProperties.PERSISTENT_BASIC,
                        oStream.toByteArray());
            } else {
                publishMessage(mqModel.getExchangeName(), routingKey, MessageProperties.BASIC,
                        oStream.toByteArray());
            }


        } else {
            throw new Exception("MQModel for "
                    + protobufMsg.getClass().getName() + " not found!");
        }

    }

    //binary protobuf message wrapper
    public void publishMessage(com.google.protobuf.Message wrapperProtoMsg, String routingKey, Class<?> msgClass) throws Exception {
        MQModel mqModel = getMQModelByMsgClassName(msgClass
                .getName());

        if (mqModel != null) {
            // Get routing key
            if (routingKey == null || routingKey.isEmpty()) {
                if (mqModel.getMqMode() == MQMode.SIMPLE) {
                    routingKey = mqModel.getQueueName();
                } else if (mqModel.getMqMode() == MQMode.FANOUT) {
                    routingKey = "";
                } else {
                    throw new Exception("Routing key curently not implemented for MQMode => Topic and Direct"); //TODO:IH: Please implement this
                }
            }

            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            wrapperProtoMsg.writeTo(oStream);

            if (mqModel.isPersistence()) {
                publishMessage(mqModel.getExchangeName(), routingKey, MessageProperties.PERSISTENT_BASIC,
                        oStream.toByteArray());
            } else {
                publishMessage(mqModel.getExchangeName(), routingKey, MessageProperties.BASIC,
                        oStream.toByteArray());
            }


        } else {
            throw new Exception("MQModel for "
                    + msgClass.getName() + " not found!");
        }

    }

    //binary protobuf message wrapper
    public void publishMessage(com.google.protobuf.Message protobufMsg, String sessionId, String routingKey) throws Exception {
        publishMessage(protobufMsg, sessionId, routingKey, null, null);
		/*MQModel mqModel = getMQModelByMsgClassName(protobufMsg.getClass()
				.getName());

		if (mqModel != null) {
			// Get routing key
			if(routingKey == null || routingKey.isEmpty())
			{
				if (mqModel.getMqMode() == MQMode.SIMPLE) {
					routingKey = mqModel.getQueueName();
				} else if (mqModel.getMqMode() == MQMode.FANOUT) {
					routingKey = "";
				} else {
					routingKey = mqModel.generateRoutingKey(protobufMsg);
				}
			}

			// Wrapper to base message
			com.google.protobuf.Message wrapperMsg = mqModel.wrapperMessage(
					protobufMsg, sessionId, deviceIds);
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			wrapperMsg.writeTo(oStream);

			if(mqModel.isPersistence())
			{
				publishMessage(mqModel.getExchangeName(), routingKey, MessageProperties.PERSISTENT_BASIC,
						oStream.toByteArray());
			}
			else
			{
				publishMessage(mqModel.getExchangeName(), routingKey, MessageProperties.BASIC,
						oStream.toByteArray());
			}


		} else {
			throw new Exception("MQModel for "
					+ protobufMsg.getClass().getName() + " not found!");
		}*/

    }

    //text based message
    public void publishMessage(String exchangeName, String routingKey, String text, boolean isPersistence) throws Exception {
        if (isPersistence) {
            publishMessage(exchangeName, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, text.getBytes());
        } else {
            publishMessage(exchangeName, routingKey, MessageProperties.TEXT_PLAIN, text.getBytes());
        }

    }

    public class PublishTask implements Runnable {

        private String exchangeName;
        private String routingKey;
        private byte[] data;
        private Channel channel;
        private AMQP.BasicProperties prop;

//		private BlockingQueue<MessageJob> queue;

        /**
         *
         */
        public PublishTask(String exchangeName, String routingKey, AMQP.BasicProperties prop, byte[] data, Channel channel) {
            super();

            this.exchangeName = exchangeName;
            this.routingKey = routingKey;
            this.data = data;
            this.channel = channel;
            this.prop = prop;
//			this.queue = queue;

        }

        private void publish(String exchangeName, String routingKey, AMQP.BasicProperties prop, byte[] data) throws IOException {
            if (channel != null)
                channel.basicPublish(exchangeName, routingKey, prop, data);

        }
		
/*		
		public void putJob(String exchangeName, String routingKey, byte[] data) throws InterruptedException 
		{
			queue.put(new MessageJob(exchangeName, routingKey, data));
		}*/

        /*	private void publish(MessageJob msgJob) throws IOException {
                publish(msgJob.getExchangeName(), msgJob.getRoutingKey(), msgJob.getData());

            }
    */
        @Override
//		public String call() {
        public void run() {
            try {
                publish(exchangeName, routingKey, prop, data);
            } catch (IOException e) {
                //TODO:IH: Handle the exception

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public class PublishTaskP implements Runnable {

        private String exchangeName;
        private String routingKey;
        private byte[] data;
        private ChannelPooled channelPooled;
        private AMQP.BasicProperties prop;

//		private BlockingQueue<MessageJob> queue;

        /**
         *
         */
        public PublishTaskP(String exchangeName, String routingKey, AMQP.BasicProperties prop, byte[] data, ChannelPooled channelPooled) {
            super();

            this.exchangeName = exchangeName;
            this.routingKey = routingKey;
            this.data = data;
            this.channelPooled = channelPooled;
            this.prop = prop;
//			this.queue = queue;

        }

        private void publish(String exchangeName, String routingKey, AMQP.BasicProperties prop, byte[] data) throws IOException {
            Channel ch = null;

            try {
                ch = channelPooled.obtain();
                ch.basicPublish(exchangeName, routingKey, prop, data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (null != ch && ch.isOpen()) {
                        channelPooled.recycle(ch);
                    }
                } catch (Exception ignore) {}

            }

        }

/*
		public void putJob(String exchangeName, String routingKey, byte[] data) throws InterruptedException
		{
			queue.put(new MessageJob(exchangeName, routingKey, data));
		}*/

        /*	private void publish(MessageJob msgJob) throws IOException {
                publish(msgJob.getExchangeName(), msgJob.getRoutingKey(), msgJob.getData());

            }
    */
        @Override
//		public String call() {
        public void run() {
            try {
                publish(exchangeName, routingKey, prop, data);
            } catch (IOException e) {
                //TODO:IH: Handle the exception

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

/*	public class MessageJob {
		private String exchangeName;
		private String routingKey;
		private byte[] data;
		
		*//**
     * @param exchangeName
     * @param routingKey
     * @param data
     *//*
		public MessageJob(String exchangeName, String routingKey, byte[] data) {
			super();
			this.exchangeName = exchangeName;
			this.routingKey = routingKey;
			this.data = data;
		}

		public String getExchangeName() {
			return exchangeName;
		}

		public String getRoutingKey() {
			return routingKey;
		}

		public byte[] getData() {
			return data;
		}
		
	}*/

}
