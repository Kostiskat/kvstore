package me.ccute.kvstore.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.ccute.kvstore.server.commands.BaseCommand;
import me.ccute.kvstore.server.storage.AOFHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class KvHandler extends SimpleChannelInboundHandler<Request> {

    private final Map<String, BaseCommand> commands;
    private final Map<String, String> db;
    private final AOFHandler aof;

    public KvHandler(Map<String, BaseCommand> commands, Map<String, String> db, AOFHandler aof) {
        this.commands = commands;
        this.db = db;
        this.aof = aof;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws IOException {
        BaseCommand cmd = commands.get(request.command.toUpperCase());
        String result;
        byte status;

        if (cmd != null) {

            result = cmd.execute(db, request.args, aof);
            status = 1; // Success
        } else {
            result = "ERR unknown command '" + request.command + "'";
            status = 0; // Error
        }

        sendResponse(ctx, status, result);
    }

    private void sendResponse(ChannelHandlerContext ctx, byte status, String message) {
        ByteBuf responseBuf = ctx.alloc().buffer();
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        responseBuf.writeByte(status);
        responseBuf.writeInt(messageBytes.length);
        responseBuf.writeBytes(messageBytes);

        ctx.writeAndFlush(responseBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}