package me.ccute.kvstore.sdk.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.ccute.kvstore.sdk.exceptions.KvConnectionException;
import me.ccute.kvstore.sdk.network.KvClientHandler;
import me.ccute.kvstore.sdk.network.RequestEncoder;
import me.ccute.kvstore.sdk.network.ResponseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyKvClient implements KvClient {

    private static final Logger log = LoggerFactory.getLogger(NettyKvClient.class);

    private final int timeoutMs;

    private final EventLoopGroup group;
    private final Channel channel;
    private final KvClientHandler handler;

    private final java.util.concurrent.atomic.AtomicLong idGenerator = new java.util.concurrent.atomic.AtomicLong(0);

    NettyKvClient(String host, int port, int timeoutMs) {
        this.timeoutMs = timeoutMs;

        this.group = new NioEventLoopGroup(2);
        this.handler = new KvClientHandler();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new io.netty.handler.flush.FlushConsolidationHandler(256, true));
                            ch.pipeline().addLast(new ResponseDecoder());
                            ch.pipeline().addLast(new RequestEncoder());
                            ch.pipeline().addLast(handler);
                        }
                    });

            this.channel = b.connect(host, port).sync().channel();
            log.info("KvClient connected to {}:{}", host, port);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KvConnectionException("Interrupted while connecting to database", e);
        } catch (Exception e) {
            group.shutdownGracefully();
            throw new KvConnectionException("Could not connect to database at " + host + ":" + port, e);
        }
    }

    @Override
    public void set(String key, String value) {
        if (key == null || key.isEmpty()) throw new IllegalArgumentException("Key cannot be null or empty");
        if (value == null) throw new IllegalArgumentException("Value cannot be null");

        execute("SET", key, value);
    }

    @Override
    public String get(String key) {
        if (key == null || key.isEmpty()) throw new IllegalArgumentException("Key cannot be null or empty");

        try {
            String result = execute("GET", key);
            return "null".equals(result) ? null : result;
        } catch (me.ccute.kvstore.sdk.exceptions.KvException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("ERR")) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public boolean delete(String key) {
        if (key == null || key.isEmpty()) throw new IllegalArgumentException("Key cannot be null or empty");

        String response = execute("DEL", key);

        return response != null && (response.equals("1") || response.equalsIgnoreCase("OK"));
    }

    @Override
    public void increment(String key, int incrementAmount) {
        if (key == null || key.isEmpty()) throw new IllegalArgumentException("Key cannot be null or empty");

        // Note: Make sure your server has an "INCR" or "INCRBY" command registered
        execute("INCR", key, String.valueOf(incrementAmount));
    }

    @Override
    public void increment(String key) {
        increment(key, 1);
    }

    @Override
    public java.util.concurrent.CompletableFuture<String> executeAsync(String command, String... args) {
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Command cannot be null or empty");
        }

        long requestId = idGenerator.incrementAndGet();
        me.ccute.kvstore.common.Request request =
                new me.ccute.kvstore.common.Request(requestId, command, args);

        java.util.concurrent.CompletableFuture<me.ccute.kvstore.common.Response> internalFuture =
                new java.util.concurrent.CompletableFuture<>();

        handler.registerRequest(requestId, internalFuture);
        channel.writeAndFlush(request);

        // Map the binary Response into a String asynchronously
        return internalFuture.thenApply(response -> {
            String responseText = new String(response.getPayload(), java.nio.charset.StandardCharsets.UTF_8);
            if (response.getStatus() == 1) {
                return responseText;
            } else {
                throw new me.ccute.kvstore.sdk.exceptions.KvException(responseText);
            }
        });
    }

    @Override
    public String execute(String command, String... args) {
        try {
            // The synchronous version just pauses the thread to wait for the async one
            return executeAsync(command, args).get(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            handler.removeRequest(idGenerator.get()); // Rough cleanup for timeout
            throw new me.ccute.kvstore.sdk.exceptions.KvTimeoutException("Command timed out after " + timeoutMs + "ms", e);
        } catch (Exception e) {
            throw new me.ccute.kvstore.sdk.exceptions.KvException("Failed to execute command: " + command, e);
        }
    }

    @Override
    public void close() {
        log.info("Shutting down KvClient connections...");
        group.shutdownGracefully();
    }
}
