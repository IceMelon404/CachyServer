package com.icemelon404.cachy.network.packet;

public enum PacketType {

    GET(1), SET(2), DELETE(3), ACK(4), NACK(5);

    private final int id;

    PacketType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static PacketType getById(int packetType) {
        for (PacketType type : PacketType.values()) {
            if (type.id == packetType)
                return type;
        }
        throw new IllegalArgumentException("유효하지 않은 패킷 타입입니다: " + packetType);
    }


}
