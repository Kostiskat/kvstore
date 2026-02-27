package me.ccute.kvstore.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.ccute.kvstore.common.Response;

public class ResponseEncoder extends MessageToByteEncoder<Response> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response response, ByteBuf byteBuf) {

        // Write 8-byte Request ID
        byteBuf.writeLong(response.requestId());

        // Write 1-byte status code
        byteBuf.writeByte(response.status());

        // Write the length of the payload
        byteBuf.writeInt(response.payload().length);

        // Wrote the payload bytes
        if (response.payload().length > 0) {
            byteBuf.writeBytes(response.payload());
        }
    }
}
