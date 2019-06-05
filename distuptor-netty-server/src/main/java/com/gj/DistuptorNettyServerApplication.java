package com.gj;

import com.gj.disruptor.MessageConsumer;
import com.gj.disruptor.RingBufferWorkEnum;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistuptorNettyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistuptorNettyServerApplication.class, args);
        MessageConsumer[] consumers = new MessageConsumer[4];
        for (int i = 0; i < consumers.length; i++) {
            MessageConsumer consumer = new ServerConsumer("code:sessionId:003");
            consumers[i] = consumer;
        }
        RingBufferWorkEnum.INSTANCE.getRingBufferWorkFactory().init(ProducerType.MULTI, 1024, new BlockingWaitStrategy(), consumers);
        new NettyServer();
    }

}
