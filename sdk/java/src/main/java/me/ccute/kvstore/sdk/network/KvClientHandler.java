package me.ccute.kvstore.sdk.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.ccute.kvstore.common.Response;
import me.ccute.kvstore.sdk.exceptions.KvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class KvClientHandler extends SimpleChannelInboundHandler<Response> {

    private static final Logger log = LoggerFactory.getLogger(KvClientHandler.class);

    private final Map<Long, CompletableFuture<Response>> pendingRequests = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) {
        CompletableFuture<Response> future = pendingRequests.remove(response.getRequestId());

        if (future != null) {
            future.complete(response);
        } else {
            log.warn("Received response for unknown request ID: {}", response.getRequestId());
        }
    }

    /**
     * Registers a new request to be tracked by the handler.
     */
    public void registerRequest(long requestId, CompletableFuture<Response> future) {
        pendingRequests.put(requestId, future);
    }

    /**
     * Removes a request from tracking.
     * This is crucial for preventing memory leaks when a request times out
     * before the server can respond.
     *
     * @param requestId The ID of the request to remove.
     */
    public void removeRequest(long requestId) {
        pendingRequests.remove(requestId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Network error in client handler", cause);
        // If the connection dies, we must fail all pending futures so user threads don't hang forever!
        KvException exception = new KvException("Network connection failed", cause);
        pendingRequests.values().forEach(future -> future.completeExceptionally(exception));
        pendingRequests.clear();
        ctx.close();
    }
}
