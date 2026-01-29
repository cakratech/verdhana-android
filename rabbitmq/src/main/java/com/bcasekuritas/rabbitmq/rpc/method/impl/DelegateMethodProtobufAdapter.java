package com.bcasekuritas.rabbitmq.rpc.method.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import com.bcasekuritas.rabbitmq.rpc.method.DelegateMethod;


/*public abstract class DelegateMethodProtobufAdapter <T extends com.google.protobuf.Message, P extends com.google.protobuf.Message, U extends com.google.protobuf.GeneratedMessage.Builder<U>> implements
		DelegateMethod<T, P> {*/
public abstract class DelegateMethodProtobufAdapter<T extends com.google.protobuf.Message, P extends com.google.protobuf.Message> implements
		DelegateMethod<T, P> {
	
	private Class<T> builderClass;

	/**
	 * 
	 */
	public DelegateMethodProtobufAdapter(final Class<T> builderClass) {
		this.builderClass = builderClass;
	}

	@Override
	public byte[] toByteArray(P obj) {
		byte[] ret = null;
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		try {
			obj.writeTo(oStream);

			ret = oStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	@Override
	public T toObject(byte[] data) {
		/*T ret = null;
		try {
			
			Method method = builderClass.getMethod("newBuilder");
			
			U builder = (U)method.invoke(null);
			
			ret = (T)builder.mergeFrom(new ByteArrayInputStream(data)).build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;*/
		
		T ret = null;
		try {
			ByteArrayInputStream iStream = new ByteArrayInputStream(data);
	    	
	        Method method = builderClass.getMethod("newBuilder");
			Object builder = method.invoke(null);
			
			method = builder.getClass().getMethod("mergeFrom", InputStream.class);
			method.invoke(builder, iStream);
			
			method = builder.getClass().getMethod("build");
 			ret = (T)method.invoke(builder);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

/*	@Override
	public Message processMethod(Message arg) {
		// TODO Auto-generated method stub
		return null;
	}
*/
}
