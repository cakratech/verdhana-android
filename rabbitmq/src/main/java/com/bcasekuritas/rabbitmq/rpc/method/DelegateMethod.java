package com.bcasekuritas.rabbitmq.rpc.method;

import com.bcasekuritas.rabbitmq.converter.BytesConverter;

public interface DelegateMethod<T,P> extends BytesConverter<T, P>
{
	 P processMethod(T arg);
}