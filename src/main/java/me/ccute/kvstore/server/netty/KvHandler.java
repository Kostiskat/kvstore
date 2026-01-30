package me.ccute.kvstore.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.ccute.kvstore.server.commands.BaseCommand;
import me.ccute.kvstore.server.storage.AOFHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        System.out.println("Handler processing: " + request.command);
        BaseCommand cmd = commands.get(request.command);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);

        if (cmd != null) {
            cmd.execute(db, request.args, out, aof);
        } else {
            out.writeByte(0);
            out.writeUTF("unknown command");
        }

        out.flush();

        ByteBuf response = Unpooled.wrappedBuffer(buffer.toByteArray());
        channelHandlerContext.writeAndFlush(response);
        System.out.println("Handler sent response.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
