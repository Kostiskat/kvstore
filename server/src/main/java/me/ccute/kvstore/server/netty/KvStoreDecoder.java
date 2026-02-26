package me.ccute.kvstore.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KvStoreDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        String cmd = readLengthPrefixedString(byteBuf);

        int argCount = byteBuf.readInt();

        List<String> args = new ArrayList<>();
        for (int i = 0; i < argCount; i++) {
            args.add(readLengthPrefixedString(byteBuf));
        }

        Request request = new Request(cmd, args.toArray(new String[0]));
        list.add(request);
    }

    private String readLengthPrefixedString(ByteBuf in) {
        int len = in.readInt();
        String s = in.toString(in.readerIndex(), len, StandardCharsets.UTF_8);
        in.skipBytes(len);
        return s;
    }
}
