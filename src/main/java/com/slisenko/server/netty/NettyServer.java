package com.slisenko.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;

public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // For accepting new connections
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // For processing I/O at existing connections
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // Netty has many different transports, one of them is NIO
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));//구분자 기반의 패킷처리
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new EchoServerHandlerV2());
                    }
                });

            ChannelFuture f = b.bind(45002).sync();
            System.out.println("Starting nio server at " + f.channel().localAddress());

            // Wait until server socket is closed
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

class EchoServerHandlerV2 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Server received " + msg);
        String response = msg + ", servertime=" + new Date().toString() + "\r\n";
        boolean exitFlag = false;
        if("exit".equals(msg.toString().toLowerCase())) {
            response = "exit...";
            exitFlag = true;
        }
        ctx.write(response);
        // IO handling method (writeAndFlush)
        ChannelFuture future = ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
//            .addListener(ChannelFutureListener.CLOSE); // Close connection after response
        if(exitFlag){
            // basic ChannelFutureListener
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
