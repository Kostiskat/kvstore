package me.ccute.kvstore.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.ccute.kvstore.server.commands.*;
import me.ccute.kvstore.server.netty.KvHandler;
import me.ccute.kvstore.server.netty.KvStoreDecoder;
import me.ccute.kvstore.server.storage.AOFHandler;
import me.ccute.kvstore.server.utils.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final ConcurrentHashMap<String, String> dbmap = new ConcurrentHashMap<>();
    private static final Map<String, BaseCommand> commands = new HashMap<>();

    // Init AOF handler
    private static final AOFHandler aof = new AOFHandler("db.aof");



    // STATIC block to register commands when the program starts
    static {
        commands.put("GET", new GetCommand());
        commands.put("SET", new SetCommand());
        commands.put("DEL", new DelCommand());
        commands.put("INCR", new IncrCommand());
    }

    static void main(String[] args) throws IOException {
        // The database's main hashmap
        String AOFPath = "data.db";

        try {
            aof.start();
            aof.recover(dbmap, commands);


            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new KvStoreDecoder());
                                socketChannel.pipeline().addLast(new KvHandler(commands, dbmap, aof));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                System.out.println(Logger.toLogMessage("Database running on port 6379."));

                b.bind(6379).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                aof.close();
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }




        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
