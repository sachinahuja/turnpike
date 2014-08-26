package io.str8.turnpike.routing;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostStandardRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class Router extends SimpleChannelInboundHandler<HttpRequest> {

    private final Logger LOG = LoggerFactory.getLogger(Router.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest req) throws Exception {

        HttpMethod method = req.method();
        String uri = req.uri();
        LOG.debug("Method : {}, URI: {}", method, uri);

        String segments[] = parseUri(uri);
        String serviceName = segments[1];
        Route route = RouteRegister.instance.getRoute(serviceName);
        if(route==null)
            throw new RuntimeException("No Routes Defined for Service : "+serviceName);


        String json = getBody((DefaultFullHttpRequest) req);
        HttpHeaders headers = req.headers();
        Set<String> names = headers.names();
        Map<String, Object> headerMap = new HashMap<String, Object>();
        for(String name: names){
            headerMap.put(name, headers.get(name));
        }

        String content = route.execute(req, Arrays.copyOfRange(segments, 2, segments.length), json, headerMap);
        ok(channelHandlerContext, content);
    }

    private String getBody(DefaultFullHttpRequest req) {
        DefaultFullHttpRequest fullReq = (DefaultFullHttpRequest)req;
        ByteBuf buf = fullReq.content();
        if(buf!=null && buf.isReadable()){
            String json = buf.toString(CharsetUtil.UTF_8);
            return json;
        }
        return null;
    }

    private void ok(ChannelHandlerContext ctx, String content){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content.getBytes()));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private String[] parseUri(String uri){

        Iterable<String> pathElements = Splitter.on('/').omitEmptyStrings().split(uri);

        if(Iterables.isEmpty(pathElements))
            throw new RuntimeException("Need at least api version and service in path");

        int peSize = Iterables.size(pathElements);
        if(peSize < 2)
            throw new RuntimeException("Need at least api version and service in path");


        String version = Iterables.getFirst(pathElements, null);
        String service = Iterables.get(pathElements, 1);
        System.out.println("API Version Requested: "+version+", Service requested: "+service);

        if(!"v1".equals(version)){
            throw new RuntimeException("Unsupported API Version");
        }

        return Iterables.toArray(pathElements, String.class);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Exception In The Handler Chain: {}",cause.getMessage());
        cause.printStackTrace();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
