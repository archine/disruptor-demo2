package com.gj;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gjing
 **/
@Slf4j
public class NettyServer {
    public NettyServer() {
        //创建两个工作线程组，一个用于接收网络请求一个用于实际处理业务
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //缓存区动态调配（自适应）
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ServerHandler());
                            socketChannel.pipeline().addLast(new HttpServerCodec());
                            //进行大数据流的写入
                            socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                            //对httpMessage进行聚合,聚合成FullHttpRequest或FullHttpResponse,几乎在netty编程中,都会用到这个
                            socketChannel.pipeline().addLast(new HttpObjectAggregator(1024 * 64));
                        }
                    });
            //绑定端口,同步等待
            ChannelFuture channelFuture = bootstrap.bind(8765).sync();
            log.warn("------Netty server starting--------");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.warn("-------Netty server shutdown--------");
        }

    }
}
