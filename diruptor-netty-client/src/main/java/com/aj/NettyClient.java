package com.aj;

import com.gj.MarshallingCodeCFactory;
import com.gj.TranslatorData;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gjing
 **/
@Slf4j
public class NettyClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8765;

    private EventLoopGroup workGroup = new NioEventLoopGroup();

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
                            //对httpMessage进行聚合,聚合成FullHttpRequest或FullHttpResponse,几乎在netty编程中,都会用到这个
                            socketChannel.pipeline().addLast(new HttpObjectAggregator(1024 * 64));
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });
            //建立连接
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();
            for(int i =0; i <10; i++){
                TranslatorData request = new TranslatorData();
                request.setId("" + i);
                request.setName("请求消息名称 " + i);
                request.setMessage("请求消息内容 " + i);
                channelFuture.channel().writeAndFlush(request.toString());
            }
            log.warn("------Netty client connecting--------");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
            log.warn("-------Netty client shutdown--------");
        }
    }
}
