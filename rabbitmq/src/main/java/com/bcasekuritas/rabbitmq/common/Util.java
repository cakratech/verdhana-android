package com.bcasekuritas.rabbitmq.common;

public class Util {

    public static String generateExchangeName(String group, com.google.protobuf.ProtocolMessageEnum type)
    {
        String ret;

        if(group!=null && !group.isEmpty())
        {
            ret = (group + "." + type.toString()).toLowerCase();
        }
        else
        {
            ret = type.toString().toLowerCase();
        }

        return ret;

    }

    public static String generateExchangeName(String group, com.google.protobuf.ProtocolMessageEnum type, String receiveType)
    {
        String ret;

        if(group!=null && !group.isEmpty())
        {
            ret = (group + "." + type.toString() + (receiveType!=null && !receiveType.isEmpty() ? "-" + receiveType: "")).toLowerCase();
        }
        else
        {
            ret = type.toString().toLowerCase();
        }

        return ret;

    }


    public static String generateRPCName(String group, com.google.protobuf.ProtocolMessageEnum type)
    {
        String ret;

        if(group!=null && !group.isEmpty())
        {
            ret = (group + "." + type.toString()).toLowerCase() + "-rpc";
        }
        else
        {
            ret = type.toString().toLowerCase() + "-rpc";
        }

        return ret;
    }
}
