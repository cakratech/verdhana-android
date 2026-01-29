package com.bcasekuritas.rabbitmq.connection;

public interface ExceptionConnection {
    void onException(String exception, String exceptionDesc);
}
