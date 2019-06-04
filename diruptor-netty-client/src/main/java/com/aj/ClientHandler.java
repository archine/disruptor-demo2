package com.aj;

import com.gj.TranslatorData;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gjing
 **/
@Slf4j
public class ClientHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            TranslatorData data = (TranslatorData) msg;
            log.info("收到数据: {}", data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //切记,用完了缓存要释放
            ReferenceCountUtil.release(msg);
        }
    }
}
