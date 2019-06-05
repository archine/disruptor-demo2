package com.gj;

import com.gj.disruptor.MessageProducer;
import com.gj.disruptor.RingBufferWorkEnum;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gjing
 **/
@Slf4j
public class ServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TranslatorData request = (TranslatorData) msg;
        MessageProducer producer = RingBufferWorkEnum.INSTANCE.getRingBufferWorkFactory().getProducer("code:sessionId:001");
        producer.sengMessage(ctx, request);
    }

}
