package com.example.sample.netty.tcp;

import com.alibaba.fastjson.JSON;
import com.example.sample.util.ErrorPrintUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

public class NettyTcpServer implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(NettyTcpServer.class);

    private int port;

    public NettyTcpServer(int port) {
        super();
        this.port = port;
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap = bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            // 1.使用 openssl 工具签名工具生成自签名证书
                            // SSLEngine sslEngine = SSLContextFactory.getSslContext().createSSLEngine();
                            // sslEngine.setUseClientMode(false);
                            // SslHandler sslHandler = new SslHandler(sslEngine);

                            // 2.使用 netty 自带类创建自签名证书
                            SelfSignedCertificate certificate = new SelfSignedCertificate();
                            SslHandler sslHandler = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build().newHandler(channel.alloc());

                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(sslHandler);
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new NettyTcpServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info(">>>>> NETTY TCP SERVER STARTING, PORT: {}", port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            ErrorPrintUtil.printErrorMsg(logger, e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        new Thread(new NettyTcpServer(port)).start();

        Map<String, String> messageMap = new HashMap<>();
        String message = "Hello! What's your name?";
        messageMap.put("message", message);
        new Thread(() -> {
            while (true) {
                Map<String, Channel> channelMap = TcpChannelManager.getChannelMap();
                messageMap.put("time", "" + Clock.systemUTC().instant());
                channelMap.keySet().forEach(id -> TcpChannelManager.sendMessage2Client(id, JSON.toJSONString(messageMap)));
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    ErrorPrintUtil.printErrorMsg(logger, e);
                }
            }
        }).start();
    }

}