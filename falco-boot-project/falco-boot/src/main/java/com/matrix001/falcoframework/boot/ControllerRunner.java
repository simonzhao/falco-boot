package com.matrix001.falcoframework.boot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ControllerRunner implements Runnable {
    private Logger logger = LoggerFactory.getLogger(ControllerRunner.class);

    private Method method;
    private Object object;
    private ChannelHandlerContext ctx;
    private HttpRequest request;

    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");

    public ControllerRunner(ChannelHandlerContext ctx, Method method, Object object, HttpRequest request){
        this.ctx = ctx;
        this.method = method;
        this.object = object;
        this.request = request;
    }
    public void run() {
        FullHttpResponse response = null;
        try {
            Class<?> returnType = method.getReturnType();
            String content = null;
            if (returnType.getName() == "java.lang.String]") {
                content = (String) method.invoke(object);
            } else {
                Object obj = method.invoke(object);
                Gson gson = new GsonBuilder().create();
                content = gson.toJson(obj);
            }
            logger.debug("ReturnType:[{}]", returnType.getName());
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content.getBytes()));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response);
        ctx.close();
    }
}
