package com.icemelon404.cachy.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteToPacketDecoder extends ByteToMessageDecoder {

    private final int HEADER_SIZE = 16;

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> list) throws Exception {
        byteBuf.markReaderIndex();
        if (byteBuf.readableBytes() >= HEADER_SIZE) {
            long packetId = byteBuf.readLong();
            int packetType = byteBuf.readInt();
            PacketType type = PacketType.getById(packetType);
            int bodyLength = byteBuf.readInt();
            if (byteBuf.readableBytes() < bodyLength) {
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] body = new byte[bodyLength];
            byteBuf.readBytes(body);
            list.add(new Packet(packetId, type, body));
        }
    }
}
