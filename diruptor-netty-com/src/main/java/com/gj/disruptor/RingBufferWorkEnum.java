package com.gj.disruptor;

/**
 * @author Gjing
 **/
public enum RingBufferWorkEnum {
    /**
     * 生成RingBufferWorkPollFactory
     */
    INSTANCE;
    public RingBufferWorkPollFactory getRingBufferWorkFactory() {
        return new RingBufferWorkPollFactory();
    }
}
