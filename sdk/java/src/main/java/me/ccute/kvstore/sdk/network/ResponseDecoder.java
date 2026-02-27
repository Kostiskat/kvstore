package me.ccute.kvstore.sdk.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import me.ccute.kvstore.common.Response;

import java.util.List;

public class ResponseDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        long requestId = in.readLong();

        byte status = in.readByte();

        int payloadLength = in.readInt();

        byte[] payload = new byte[payloadLength];
        if (payloadLength > 0) {
            in.readBytes(payload);
        }

        out.add(new Response(requestId, status, payload));
    }
}