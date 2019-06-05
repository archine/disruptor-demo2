package com.gj;

import com.gj.disruptor.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gjing
 **/
@Slf4j
public class ServerConsumer extends MessageConsumer {
    public ServerConsumer(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataDto e) throws Exception {
        TranslatorData translatorData = e.getTranslatorData();
        System.out.println("Server: " + translatorData.toString());

        TranslatorData response = new TranslatorData();
        response.setId("resp: " + translatorData.getId());
        response.setMessage("resp: " + translatorData.getMessage());
        response.setName("resp: " + translatorData.getName());
        e.getCtx().writeAndFlush(response);
    }
}
