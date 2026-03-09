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
import me.ccute.kvstore.server.netty.ResponseEncoder;
import me.ccute.kvstore.server.storage.AOFHandler;
import me.ccute.kvstore.server.storage.BoundedConcurrentMap;
import me.ccute.kvstore.server.utils.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private static final int PORT = ConfigLoader.getInt("port", 6379);
    private static final String BIND_IP = ConfigLoader.getString("bind", "127.0.0.1");
    private static final int MAX_KEYS = ConfigLoader.getInt("max_memory_keys", 1_000_000);
    private static final String AOF_PATH = ConfigLoader.getString("aof_path", "db.aof");


    private static final Map<String, String> dbmap = new BoundedConcurrentMap<>(MAX_KEYS);
    private static final Map<String, BaseCommand> commands = new HashMap<>();

    // Init AOF handler
    private static final AOFHandler aof = new AOFHandler(AOF_PATH);



    // STATIC block to register commands when the program starts
    static {
        commands.put("GET", new GetCommand());
        commands.put("SET", new SetCommand());
        commands.put("DEL", new DelCommand());
        commands.put("INCR", new IncrCommand());
        commands.put("EXISTS", new ExistsCommand());
    }

    static void main() {

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
                            protected void initChannel(SocketChannel socketChannel) {
                                socketChannel.pipeline().addLast(new KvStoreDecoder());
                                socketChannel.pipeline().addLast(new ResponseEncoder());
                                socketChannel.pipeline().addLast(new KvHandler(commands, dbmap, aof));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    logger.info("SIGTERM received. Initiating graceful shutdown...");
                    try {
                        bossGroup.shutdownGracefully().sync();
                        workerGroup.shutdownGracefully().sync();

                        aof.close();

                        logger.info("Database shut down!");
                    } catch (InterruptedException | IOException e) {
                        System.err.println("Shutdown interrupted: " + e.getMessage());
                    }
                }));
                logger.info("Database running on port {}", PORT);

                b.bind(BIND_IP, PORT).sync().channel().closeFuture().sync();
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
