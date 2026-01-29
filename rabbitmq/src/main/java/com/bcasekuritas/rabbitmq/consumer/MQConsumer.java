package com.bcasekuritas.rabbitmq.consumer;

import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.bcasekuritas.rabbitmq.message.MQMessageEvent;
import com.bcasekuritas.rabbitmq.message.MQMessageListener;
import com.bcasekuritas.rabbitmq.pool.ChannelPooled;
import timber.log.Timber;

public class MQConsumer<P extends com.google.protobuf.Message> {
//    private static final String DIRECT_QUEUE = "amq.rabbitmq.reply-to";
//    private static final String SERVER_QUEUE = "server-rpc-queue";

    private String uri;
    private String mqMode;
    private String name;
    private String exchangeName;
    private String queueName;

    private boolean durable = false;
    private boolean autoDelete = false;
//	private boolean persistence = false;

    private Connection conn = null;
    private ChannelPooled pool;
    private Channel ch = null;

    private MQMessageListener<P> mqMsgListener;

    private Class<P> builderClass;

    private String ctag;


    public MQConsumer(String name, String mqMode, String uri, String userName, String password, final Class<P> builderClass,
                      boolean durable, boolean autoDelete) throws Exception {
        this.name = name;
        this.mqMode = mqMode;

//		this.exchangeName = name;
        this.uri = uri;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(this.uri);
        factory.setUsername(userName);
        factory.setPassword(password);

        this.conn = factory.newConnection();

        this.builderClass = builderClass;

        this.durable = durable;
        this.autoDelete = autoDelete;
//		this.persistence = persistence;

        initConn();
    }

    public MQConsumer(String name, String mqMode, Connection conn, final Class<P> builderClass,
                      boolean durable, boolean autoDelete) throws Exception {
        this.name = name;
        this.mqMode = mqMode;

//		this.exchangeName = name;
        this.conn = conn;

        this.builderClass = builderClass;

        this.durable = durable;
        this.autoDelete = autoDelete;
//		this.persistence = persistence;

        initConn();
    }

    public MQConsumer(String name, String mqMode, ChannelPooled pool, final Class<P> builderClass,
                      boolean durable, boolean autoDelete) throws Exception {
        this.name = name;
        this.mqMode = mqMode;

//		this.exchangeName = name;
        this.pool = pool;

        this.builderClass = builderClass;

        this.durable = durable;
        this.autoDelete = autoDelete;
//		this.persistence = persistence;

        initConn();
    }

    public MQConsumer() {
    }


    private void initConn() throws Exception {
        if (null == pool) {
            Log.d("initConn", "using create channel");
            ch = conn.createChannel();
        } else {
            Log.d("initConn", "using pool obtain");
            ch = pool.obtain();
        }

        if (this.mqMode.equalsIgnoreCase("simple")) {
            queueName = name;

            ch.queueDeclare(queueName, durable, false, autoDelete, null);
        } else {
            exchangeName = name;

            ch.exchangeDeclare(exchangeName, mqMode, durable, autoDelete, null);
//            queueName = ch.queueDeclare().getQueue();

            queueName = UUID.randomUUID().toString().toUpperCase();
            ch.queueDeclare(queueName, false, false, true, null);

            Log.d("initConn", "queueName: " + queueName + " - exchangeName: " + exchangeName);
        }


    }

    public void setMqMsgListener(MQMessageListener<P> listener) {
        Log.d("MQConsumer", "MQ Set listener - queueName: " + queueName + " - exchangeName: " + exchangeName);
        this.mqMsgListener = listener;
    }

    public void consume() throws Exception {
        if (ch == null) {
            throw new Exception("consume Listener: Ch null.");
        }
        if (!ch.isOpen()) {
            throw new Exception("consume Listener: Ch close.");
        }
        Log.d("MQConsumer", "Basic Consume - queueName: " + queueName + " - exchangeName: " + exchangeName);
        Log.d("channelMq", "Channel Number Exchange " + String.valueOf(ch.getChannelNumber()) + "  " + exchangeName);

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-cancel-on-ha-failover", false);

//	        ch.basicConsume(queueName, true, args, new DefaultConsumer(ch) {
        ctag = ch.basicConsume(queueName, false, args, new DefaultConsumer(ch) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    ByteArrayInputStream iStream = new ByteArrayInputStream(body);

                    Method method = builderClass.getMethod("newBuilder");
                    Object builder = method.invoke(null);

                    method = builder.getClass().getMethod("mergeFrom", InputStream.class);
                    method.invoke(builder, iStream);

                    method = builder.getClass().getMethod("build");
//		 			P msg = (P)method.invoke(builder, null);
                    P msg = (P) method.invoke(builder);

                    if (mqMsgListener != null) {
                        mqMsgListener.messageReceived(new MQMessageEvent<P>(this, msg));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ch.basicAck(envelope.getDeliveryTag(), false);
                }

            }
        });
    }

    public void stopConsume() throws Exception {
        if (ch == null) {
            throw new Exception("stopConsume: Ch null.");
        }
        if (!ch.isOpen()) {
            throw new Exception("stopConsume: Ch close.");
        }

        Log.d("stopConsume", "queueName: " + queueName + " - exchangeName: " + exchangeName);
        if (ctag == null || ctag.isEmpty())
            throw new Exception("ctag is null or empty");
        try {
            ch.basicCancel(ctag);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        } finally {
            try {
                if (null == pool)
                    ch.close();
            } catch (Exception e) {
                // ignored
            }
            try {
                if (null != ch) {
                    pool.recycle(ch);
                }
            } catch (Exception e) {
                // ignored
            }
        }

    }

    public void subscribe(String routingKey) throws Exception {
        if (ch == null) {
            throw new Exception("subscribe: Ch null.");
        }
        if (!ch.isOpen()) {
            throw new Exception("subscribe: Ch close.");
        }
        Timber.d("MQ Subcribe RoutingKey :" + routingKey);
        if (!this.mqMode.equalsIgnoreCase("simple")) {
            if (ch.isOpen()) {
                ch.queueBind(queueName, exchangeName, routingKey);
            }
        } else {
            throw new Exception("mqMode: " + this.mqMode + " subscribe not allowed");
        }

    }

    public void unsubscribe(String routingKey) throws Exception {
        if (ch == null) {
            throw new Exception("unsubscribe: Ch null.");
        }
        if (!ch.isOpen()) {
            throw new Exception("unsubscribe: Ch close.");
        }
        Timber.d("MQ Unsubcribe RoutingKey :" + routingKey);
        if (!this.mqMode.equalsIgnoreCase("simple")) {
            ch.queueUnbind(queueName, exchangeName, routingKey);
        } else {
            throw new Exception("mqMode: " + this.mqMode + " unsubscribe not allowed");
        }

    }

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

	/*public boolean isPersistence() {
		return persistence;
	}

	public void setPersistence(boolean persistence) {
		this.persistence = persistence;
	}*/

}
