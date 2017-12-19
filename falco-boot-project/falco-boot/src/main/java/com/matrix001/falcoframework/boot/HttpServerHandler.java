package com.matrix001.falcoframework.boot;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    private static Map<String, Method> requestMap = new HashMap<String, Method>();
    private static Map<String, Object> clesses = new HashMap<String, Object>();


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if (msg instanceof HttpRequest){
            HttpRequest req = (HttpRequest)msg;

            String url = req.uri();
            logger.info("URI:[{}]", url);

            Method method = requestMap.get(url);
            if(method != null) {
                Class<?> className = method.getDeclaringClass();
                Object object = clesses.get(className.getName());

                JobPool.getPool().execute(new ControllerRunner(ctx, method, object, req));
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }

    public static void addRequest(String url, Method method){
        requestMap.put(url, method);
    }

    public static void addClass(String className, Object object){
        clesses.put(className, object);
    }
}
