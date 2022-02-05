package com.icemelon404.cachy.network.packet.get;

import com.icemelon404.cachy.network.get.GetMessage;
import com.icemelon404.cachy.network.get.GetMessageHandler;
import com.icemelon404.cachy.network.packet.PacketHandler;
import com.icemelon404.cachy.network.packet.Packet;
import com.icemelon404.cachy.network.packet.PacketType;
import io.netty.channel.ChannelHandlerContext;

public class GetPacketHandler implements PacketHandler {

    private final GetMessageHandler handler;

    public GetPacketHandler(GetMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handlePacket(ChannelHandlerContext context, Packet packet) {
        String key = new String(packet.body);
        handler.handleGet(new GetMessage(key), new GetAck(context, packet.id));
    }

    @Override
    public PacketType supportedType() {
        return PacketType.GET;
    }
}
