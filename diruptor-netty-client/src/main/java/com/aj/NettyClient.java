package com.aj;

import com.gj.TranslatorData;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @author Gjing
 **/
@Slf4j
public class NettyClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8765;

    private Channel channel;
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture;


    public NettyClient() {
        this.connect();
    }

    private void connect() {
        //创建工作线程组用于实际处理业务
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    //缓存区动态调配（自适应）
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientHandler());
                            socketChannel.pipeline().addLast(new HttpServerCodec());
                            //进行大数据流的写入
                            socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                            //对httpMessage进行聚合,聚合成FullHttpRequest或FullHttpResponse,几乎在netty编程中,都会用到这个
                            socketChannel.pipeline().addLast(new HttpObjectAggregator(1024 * 64));
                        }
                    });
            //建立连接
            this.channelFuture = bootstrap.connect(HOST, PORT).sync();
            log.warn("------Netty client connecting--------");
            this.channel = channelFuture.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    NettyClient sendMessage() {
        for (int i = 0; i < 5; i++) {
            TranslatorData translatorData = new TranslatorData();
            translatorData.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            translatorData.setMessage("客户端: " + i + "发送消息");
            translatorData.setName("客户端" + i);
            this.channel.writeAndFlush(translatorData);
        }
        return this;
    }

    void close() throws InterruptedException {
        channelFuture.channel().closeFuture().sync();
        workGroup.shutdownGracefully();
        log.warn("-------Netty client shutdown--------");
    }
}
