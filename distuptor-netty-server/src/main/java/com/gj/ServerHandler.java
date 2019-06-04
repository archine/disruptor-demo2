package com.gj;

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
        log.info("server端：{}", request.toString());
        TranslatorData response = new TranslatorData();
        response.setId("resp: " + request.getId());
        ctx.writeAndFlush(response);
    }
}
