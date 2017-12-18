package com.matrix001.falcoframework.boot;

import com.matrix001.falcoframework.context.ConfigurableApplicationContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FalcoApplication {
    private Class<?> primarySource;
    private Logger logger = LoggerFactory.getLogger(FalcoApplication.class);

    public FalcoApplication(Class<?> primarySource) {
        this.primarySource = primarySource;
    }

    public ConfigurableApplicationContext run(String... args) {
        ConfigurableApplicationContext context = null;
        logger.info("Hello World!");

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new FalcoChannelInitializer());

            Channel ch = b.bind(8080).sync().channel();
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        return context;
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return new FalcoApplication(primarySource).run(args);
    }
}