package me.ccute.kvstore.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;


import java.util.ArrayList;
import java.util.List;

public class KvStoreDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        System.out.println("Decoder received bytes: " + byteBuf.readableBytes());
        String cmd = readUTF(byteBuf);

        int argCount = byteBuf.readInt();

        List<String> args = new ArrayList<>();
        for(int i = 0; i < argCount; i++) {
            args.add(readUTF(byteBuf));
        }

        Request request = new Request(cmd, args.toArray(new String[0]));
        System.out.println("Decoder finished one packet. Command: " + cmd);
        list.add(request);
    }

    private String readUTF(ByteBuf in) {
        int len = in.readUnsignedShort(); // Java's writeUTF uses a short for length
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        return new String(bytes); // Convert to String
    }
}
