package io.str8.turnpike.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.str8.turnpike.routing.RouteLoader;

public class Server {

    private RouteLoader loader;

    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ApiInitializer());
            System.out.println("Server Starting Now.");
            b.bind(9999).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public Server(RouteLoader loader){
        this.loader = loader;
        this.loader.loadRoutes();
    }

    public static void main(String[] args) throws Exception{

        new Server(new RouteLoader() {
            @Override
            public void loadRoutes() {

            }
        }).run();
    }

}
