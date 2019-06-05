package com.gj;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Gjing
 **/
@Setter
@Getter
public class TranslatorDataDto implements Serializable {
    private TranslatorData translatorData;

    private ChannelHandlerContext ctx;

}
