package com.gj.disruptor;

import com.gj.TranslatorData;
import com.gj.TranslatorDataDto;
import com.lmax.disruptor.RingBuffer;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Gjing
 * 生产者
 **/
public class MessageProducer{

    private String producerId;

    private RingBuffer<TranslatorDataDto> ringBuffer;

    MessageProducer(RingBuffer<TranslatorDataDto> ringBuffer,String producerId) {
        this.ringBuffer = ringBuffer;
        this.producerId = producerId;
    }

    public void sengMessage(ChannelHandlerContext channelHandlerContext, TranslatorData translatorData) {
        long sequence = this.ringBuffer.next();
        try {
            TranslatorDataDto translatorDataDto = ringBuffer.get(sequence);
            translatorDataDto.setTranslatorData(translatorData);
            translatorDataDto.setCtx(channelHandlerContext);
        }finally {
            ringBuffer.publish(sequence);
        }
    }
}
