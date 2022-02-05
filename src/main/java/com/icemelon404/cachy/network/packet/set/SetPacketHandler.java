package com.icemelon404.cachy.network.packet.set;

import com.icemelon404.cachy.network.packet.Packet;
import com.icemelon404.cachy.network.packet.PacketHandler;
import com.icemelon404.cachy.network.packet.PacketType;
import com.icemelon404.cachy.network.set.SetMessage;
import com.icemelon404.cachy.network.set.SetMessageHandler;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;

public class SetPacketHandler implements PacketHandler {

    private final SetMessageHandler handler;

    public SetPacketHandler(SetMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handlePacket(ChannelHandlerContext context, Packet packet) {
        ByteBuffer buff = ByteBuffer.wrap(packet.body);
        byte tombStone = buff.get();
        int keyLen = buff.getInt();
        byte[] keyBytes = new byte[keyLen];
        buff.get(keyBytes);
        String key = new String(keyBytes);

        SetAck ack = new SetAck(packet.id, context);
        if (tombStone == (byte)1) {
            handler.handleSet(new SetMessage(key, null), ack);
            return;
        }

        int valueLen = buff.getInt();
        byte[] valueBytes = new byte[valueLen];
        buff.get(valueBytes);
        handler.handleSet(new SetMessage(key, valueBytes), ack);
    }

    @Override
    public PacketType supportedType() {
        return PacketType.SET;
    }
}
