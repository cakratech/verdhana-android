package com.bcasekuritas.rabbitmq.common;

public class Constant {

    public enum MQMode {
        SIMPLE, FANOUT, DIRECT, TOPIC
    }

    public enum ConfigType {
        SIMPLE, PROTOBUF
    }

    public static final String FLOW_MODEL_SIMPLE = "simple";
    public static final String FLOW_MODEL_FANOUT = "fanout";
    public static final String FLOW_MODEL_DIRECT = "direct";
    public static final String FLOW_MODEL_TOPIC = "topic";

}
