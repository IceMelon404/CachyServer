package com.icemelon404.cachy.network.packet.set;

import com.icemelon404.cachy.network.packet.Packet;
import com.icemelon404.cachy.network.packet.PacketType;
import com.icemelon404.cachy.network.set.Acknowledgement;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SetAck implements Acknowledgement {

    private final long receivedPacketId;
    private final ChannelHandlerContext context;
    private final int HEADER_LEN = 4;

    public SetAck(long receivedPacketId, ChannelHandlerContext context) {
        this.receivedPacketId = receivedPacketId;
        this.context = context;
    }

    @Override
    public void ack() {
        context.write(new Packet(receivedPacketId, PacketType.ACK, null));
    }

    @Override
    public void nack(Throwable error) {
        ByteBuffer buffer= ByteBuffer.allocate(HEADER_LEN + error.toString().length());
        buffer.putInt(error.toString().length());
        buffer.put(error.toString().getBytes(StandardCharsets.UTF_8));
        context.write(new Packet(receivedPacketId, PacketType.NACK, buffer.array()));
    }
}
