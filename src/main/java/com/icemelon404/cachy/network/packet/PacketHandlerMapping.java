package com.icemelon404.cachy.network.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PacketHandlerMapping extends SimpleChannelInboundHandler<Packet> {

    private final Map<PacketType, PacketHandler> handlerMap;

    public PacketHandlerMapping(Collection<PacketHandler> handlers) {
        handlerMap = new HashMap<>();
        handlers.forEach(it-> handlerMap.put(it.supportedType(), it));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        PacketHandler handler = handlerMap.get(packet.packetType);
        handler.handlePacket(channelHandlerContext, packet);
    }
}
