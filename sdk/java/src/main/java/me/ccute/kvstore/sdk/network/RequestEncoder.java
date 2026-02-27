package me.ccute.kvstore.sdk.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.ccute.kvstore.common.Request;

import java.nio.charset.StandardCharsets;

public class RequestEncoder extends MessageToByteEncoder<Request> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Request request, ByteBuf byteBuf) {
        byteBuf.writeLong(request.requestId);
        writeLengthPrefixedString(byteBuf, request.command);
        byteBuf.writeInt(request.args.length);

        for (String arg : request.args) {
            writeLengthPrefixedString(byteBuf, arg);
        }
    }

    /**
     * Helper method to write strings safely so the Server knows exactly
     * how many bytes to read before converting them back to text.
     */
    private void writeLengthPrefixedString(ByteBuf out, String str) {
        if (str == null) {
            out.writeInt(0);
            return;
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length); // The length header
        out.writeBytes(bytes);      // The actual string data
    }
}
