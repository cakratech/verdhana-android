/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bcasekuritas.rabbitmq.message;

import com.google.protobuf.Message;

/**
 * @author DELL
 */
public interface MQMessageListener<T extends Message> {

    void messageReceived(MQMessageEvent<T> event);

}
