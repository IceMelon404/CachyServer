package com.icemelon404.cachy.network.packet;

public class Packet {
    public long id;
    public PacketType packetType;
    public byte[] body;

    public Packet(long id, PacketType packetType, byte[] body) {
        this.id = id;
        this.packetType = packetType;
        this.body = body;
    }
}
