package com.icemelon404.cachy.network.packet;

import io.netty.channel.ChannelHandlerContext;

public interface PacketHandler {
    void handlePacket(ChannelHandlerContext context, Packet packet);
    PacketType supportedType();
}
