package com.bcasekuritas.rabbitmq.converter.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import com.bcasekuritas.rabbitmq.converter.BytesConverter;


public class ProtobufConverter<T extends com.google.protobuf.Message, P extends com.google.protobuf.Message> implements BytesConverter<T, P> {
	private Class<P> builderClass;

	/**
	 * 
	 */
	public ProtobufConverter(final Class<P> builderClass) {
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
 			ret = (T)method.invoke(builder, (Object) null);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	
	}

}
