package com.icemelon404.cachy.network.packet;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketToByteEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        long id = packet.id;
        byteBuf.writeLong(id);

        int packetType = packet.packetType.getId();
        byteBuf.writeInt(packetType);

        if (packet.body != null) {
            int bodyLength = packet.body.length;
            byte[] body = packet.body;
            byteBuf.writeInt(bodyLength);
            byteBuf.writeBytes(body);
        }
    }
}
