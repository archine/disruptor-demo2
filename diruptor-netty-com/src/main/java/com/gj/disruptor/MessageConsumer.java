package com.gj.disruptor;

import com.gj.TranslatorDataDto;
import com.lmax.disruptor.WorkHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Gjing
 * 消费者
 **/
@Getter
@Setter
public abstract class MessageConsumer implements WorkHandler<TranslatorDataDto> {
    protected String consumerId;

    public MessageConsumer(String consumerId) {
        this.consumerId = consumerId;
    }
}
