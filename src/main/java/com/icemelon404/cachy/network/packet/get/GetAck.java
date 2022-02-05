package com.icemelon404.cachy.network.packet.get;

import com.icemelon404.cachy.network.get.Acknowledgement;
import com.icemelon404.cachy.network.packet.Packet;
import com.icemelon404.cachy.network.packet.PacketType;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class GetAck implements Acknowledgement {

    private final ChannelHandlerContext context;
    private final int ACK_HEADER_LEN = 4;
    private final long receivedPacketId;

    public GetAck(ChannelHandlerContext context, long receivedPacketId) {
        this.context = context;
        this.receivedPacketId = receivedPacketId;
    }

    @Override
    public void ack(byte[] value) {
        ByteBuffer buffer = ByteBuffer.allocate(value.length + ACK_HEADER_LEN);
        buffer.putInt(value.length);
        buffer.put(value);
        context.write(new Packet(receivedPacketId, PacketType.ACK, buffer.array()));
    }

    @Override
    public void nack(Throwable exception) {
        ByteBuffer buffer = ByteBuffer.allocate(ACK_HEADER_LEN + exception.toString().length());
        buffer.putInt(exception.toString().length());
        buffer.put(exception.toString().getBytes(StandardCharsets.UTF_8));
        context.write(new Packet(receivedPacketId, PacketType.NACK, buffer.array()));
    }
}
