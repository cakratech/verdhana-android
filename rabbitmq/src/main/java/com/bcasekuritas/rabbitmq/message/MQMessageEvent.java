/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bcasekuritas.rabbitmq.message;

import java.util.EventObject;

//import com.tech.cakra.datafeed.client.df.message.proto.DFDictionary.DFMessage;

/**
 *
 * @author DELL
 */
public class MQMessageEvent <T extends com.google.protobuf.Message> extends EventObject {

/**
	 *
	 */
	private static final long serialVersionUID = 7835098871907865305L;
	//    private com.google.protobuf.Message protoMsg;
	
	private T protoMsg;

//    public MQMessageEvent(Object arg0, com.google.protobuf.Message protoMsg) {
	public MQMessageEvent(Object arg0, T protoMsg) {
        super(arg0);

        this.protoMsg = protoMsg;
    }

//	public com.google.protobuf.Message getProtoMsg() {
	public T getProtoMsg() {
		return protoMsg;
	}

}
