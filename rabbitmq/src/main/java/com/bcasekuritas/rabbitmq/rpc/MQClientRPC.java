package com.bcasekuritas.rabbitmq.rpc;

import android.util.Log;

import com.google.protobuf.Message;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.bcasekuritas.rabbitmq.connection.IMQConnectionListener;
import com.bcasekuritas.rabbitmq.pool.ChannelPooled;
import timber.log.Timber;


public class MQClientRPC<P extends Message, T extends Message> {
    private static final String DIRECT_QUEUE = "amq.rabbitmq.reply-to";
//    private static final String SERVER_QUEUE = "server-rpc-queue";

    private String uri;
    private String user;
    private String pass;

    private Connection conn = null;
    //    private Channel ch = null;
    private String rpcName;

    private Class<T> builderClass;

    private ChannelPooled pool;

    public MQClientRPC(String uri, String user, String pass, String rpcName, Class<T> builderClass) throws Exception {
        this.uri = uri;
        this.user = user;
        this.pass = pass;

        this.rpcName = rpcName;

        this.builderClass = builderClass;

        initConn();
    }

    public MQClientRPC(Connection conn, String rpcName, Class<T> builderClass) {
        this.conn = conn;

        this.rpcName = rpcName;

        this.builderClass = builderClass;
    }

    public MQClientRPC(ChannelPooled pool, String rpcName, Class<T> builderClass) {
        this.pool = pool;

        this.rpcName = rpcName;

        this.builderClass = builderClass;
    }

    private void initConn() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(this.uri);
        factory.setUsername(user);
        factory.setPassword(pass);

        conn = factory.newConnection();
//        ch = conn.createChannel();

    }

    public T doCall(P argument, int timeout, IMQConnectionListener imqConnectionListener) throws Exception {
        final CountDownLatch[] latch = new CountDownLatch[1];

        Channel ch = null;

        try {
            if (null == pool) {
                ch = conn.createChannel();
            } else {
                ch = pool.obtain();
                Log.d("channelMq", "Channel Number " + String.valueOf(ch.getChannelNumber()) + "  " + rpcName);
            }
			
			
	        /* String ctag = ch.basicConsume(DIRECT_QUEUE, true, new DefaultConsumer(ch) {
	            @Override
	            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
	            	ByteArrayInputStream iStream = new ByteArrayInputStream(body);
	            	
	            	DFMessage.Builder msgBuilder = DFMessage.newBuilder();
	            	msgBuilder.mergeFrom(iStream);
	                
	                result = msgBuilder.build();

	            	latch[0].countDown();
	            }
	        }); */

            ClientConsumer cs = new ClientConsumer(latch);

            latch[0] = new CountDownLatch(1);

            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-cancel-on-ha-failover", false);

            String ctag = ch.basicConsume(DIRECT_QUEUE, true, args, cs);

            AMQP.BasicProperties props = MessageProperties.MINIMAL_BASIC.builder().replyTo(DIRECT_QUEUE).build();

            ByteArrayOutputStream oStream = new ByteArrayOutputStream();
            argument.writeTo(oStream);

            ch.basicPublish("", rpcName, props, oStream.toByteArray());
            latch[0].await(timeout, TimeUnit.MILLISECONDS);
            ch.basicCancel(ctag);

            return cs.getResult();

        } catch (Exception e) {
            Timber.e("RuntimeException: " + e.getCause());
            if (e instanceof InterruptedException) {
                imqConnectionListener.getTimeOutLiveData().postValue(rpcName);
                Timber.e("rpcTimeOut: " + rpcName);
            } else {
                Timber.e(e.getMessage() + ": " + rpcName);
            }
            return null;

        } finally {
            try {
                if (null == pool)
                    ch.close();
            } catch (Exception ignore) {}
            try {
                if (null != ch && ch.isOpen()) {
                    pool.recycle(ch);
                }
            } catch (Exception ignore) {}

        }
    }

    private class ClientConsumer implements Consumer {
        private final CountDownLatch[] latch;
        private T result;

        public ClientConsumer(CountDownLatch[] latch) {
            this.latch = latch;
        }

        @Override
        public void handleConsumeOk(String consumerTag) {
        }

        @Override
        public void handleCancelOk(String consumerTag) {
        }

        @Override
        public void handleCancel(String consumerTag) throws IOException {
        }

        @Override
        public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        }

        @Override
        public void handleRecoverOk(String consumerTag) {
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        	/*ByteArrayInputStream iStream = new ByteArrayInputStream(body);
        	
        	DFMessage.Builder msgBuilder = DFMessage.newBuilder();
        	msgBuilder.mergeFrom(iStream);
            
            result = msgBuilder.build();*/

            try {
                ByteArrayInputStream iStream = new ByteArrayInputStream(body);
			    	
		           /* Method method = builderClass.getMethod("newBuilder");
		 			Object builder = method.invoke(null);
		 			
		 			method = builder.getClass().getMethod("mergeFrom", ByteArrayInputStream.class);
		 			result = (P)method.invoke(builder, iStream);*/

                Method method = builderClass.getMethod("newBuilder");
                Object builder = method.invoke(null);

                method = builder.getClass().getMethod("mergeFrom", InputStream.class);
                method.invoke(builder, iStream);

                method = builder.getClass().getMethod("build");
                result = (T) method.invoke(builder);


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            latch[0].countDown();
        }

        public T getResult() {
            return result;
        }
    }
}
