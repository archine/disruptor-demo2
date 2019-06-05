package com.aj;

import com.gj.TranslatorData;
import com.gj.TranslatorDataDto;
import com.gj.disruptor.MessageConsumer;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gjing
 **/
@Slf4j
public class ClientConsumer extends MessageConsumer {
    public ClientConsumer(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataDto translatorDataDto) throws Exception {
        TranslatorData response = translatorDataDto.getTranslatorData();
        try {
            log.info("client收到数据：" + response.toString());
        }finally {
            //释放对象
            ReferenceCountUtil.release(translatorDataDto);
        }
    }
}
