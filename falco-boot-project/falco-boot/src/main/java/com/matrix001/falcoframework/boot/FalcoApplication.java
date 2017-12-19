package com.matrix001.falcoframework.boot;

import com.matrix001.falcoframework.context.ConfigurableApplicationContext;
import com.matrix001.falcoframework.util.ClassUtils;
import com.matrix001.falcoframework.web.bind.annotation.RequestMapping;
import com.matrix001.falcoframework.web.bind.annotation.RestController;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class FalcoApplication {
    private Class<?> primarySource;
    private Logger logger = LoggerFactory.getLogger(FalcoApplication.class);

    public FalcoApplication(Class<?> primarySource) {
        this.primarySource = primarySource;
    }

    public ConfigurableApplicationContext run(String... args) {
        ConfigurableApplicationContext context = null;
        logger.info("Hello World!");

        Package packages = this.primarySource.getPackage();
        logger.debug("Package:[{}]", packages.getName());

        Reflections reflections = new Reflections(packages.getName()
                , new MethodAnnotationsScanner()
                , new TypeAnnotationsScanner()
                , new SubTypesScanner()
                , new MethodParameterScanner()
                , new MethodParameterNamesScanner()
        );
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RestController.class);

        for(Class<?> clazz : classes){
            logger.info("Class:[{}]", clazz.getName());
        }
        logger.info("Class num:[{}]", classes.size());

        Set<Method> methods = reflections.getMethodsAnnotatedWith(RequestMapping.class);
        for(Method method : methods){
            Class<?> clazz = method.getDeclaringClass();
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            String[] paths = requestMapping.value();
            for(String path : paths) {
                logger.debug("Request:[{}] by [{}.{}()]", path, clazz.getName(), method.getName());

                HttpServerHandler.addRequest(path, method);
                try {
                    HttpServerHandler.addClass(clazz.getName(), clazz.newInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        JobPool.init(8);

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
            ExecutorService pool = JobPool.getPool();
            pool.shutdown();
        }
        return context;
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return new FalcoApplication(primarySource).run(args);
    }
}