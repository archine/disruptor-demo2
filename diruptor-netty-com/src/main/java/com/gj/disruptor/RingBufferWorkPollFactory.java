package com.gj.disruptor;

import com.gj.TranslatorDataDto;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author Gjing
 **/
public class RingBufferWorkPollFactory {
    /**
     * 消费者
     */
    private static Map<String, MessageConsumer> consumerMap = new ConcurrentHashMap<>();
    /**
     * 生产者
     */
    private static Map<String, MessageProducer> producerMap = new ConcurrentHashMap<>();

    private RingBuffer<TranslatorDataDto> ringBuffer;

    private WorkerPool<TranslatorDataDto> workerPool;

    private SequenceBarrier sequenceBarrier;

    public void init(ProducerType producerType, int ringBufferSize, WaitStrategy waitStrategy, MessageConsumer[] messageConsumers) {
        this.ringBuffer = RingBuffer.create(producerType, new EventFactory<TranslatorDataDto>() {
            @Override
            public TranslatorDataDto newInstance() {
                return new TranslatorDataDto();
            }
        }, ringBufferSize, waitStrategy);
        this.sequenceBarrier = this.ringBuffer.newBarrier();
        this.workerPool = new WorkerPool<>(this.ringBuffer, this.sequenceBarrier, new ExHandler(), messageConsumers);
        //把构建的消费者丢到内存中
        Stream.of(messageConsumers).forEach(e->{
            consumerMap.put(e.getConsumerId(), e);
        });
        //添加sequence
        this.ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());
        //启动
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 10, 100, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(7));
        this.workerPool.start(executor);
    }

    /**
     * 获取生产者
     * @param producerId 生产者id
     * @return producer
     */
    public MessageProducer getProducer(String producerId) {
        MessageProducer producer = producerMap.get(producerId);
        if (producer == null) {
            producer = new MessageProducer(this.ringBuffer,producerId);
            producerMap.put(producerId, producer);
        }
        return producer;
    }

    /**
     * 异常处理
     */
    static class ExHandler implements ExceptionHandler<TranslatorDataDto> {
        @Override
        public void handleEventException(Throwable throwable, long l, TranslatorDataDto translatorDataDto) {

        }

        @Override
        public void handleOnStartException(Throwable throwable) {

        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {

        }
    }

}
