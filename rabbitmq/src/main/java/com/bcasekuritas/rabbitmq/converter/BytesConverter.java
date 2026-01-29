package com.bcasekuritas.rabbitmq.converter;

public interface BytesConverter<T, P> {
	
	byte[] toByteArray(P obj);
	T toObject(byte[] data);

}
