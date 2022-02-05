package com.icemelon404.cachy.network;

import com.icemelon404.cachy.network.packet.ByteToPacketDecoder;
import com.icemelon404.cachy.network.packet.PacketHandler;
import com.icemelon404.cachy.network.packet.PacketHandlerMapping;
import com.icemelon404.cachy.network.flush.FlushingOutBoundHandler;
import com.icemelon404.cachy.network.packet.PacketToByteEncoder;
import com.icemelon404.cachy.network.packet.PacketType;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.Collection;
import java.util.Map;

public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Collection<PacketHandler> handlerMap;

    public DefaultChannelInitializer(Collection<PacketHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(
                new FlushingOutBoundHandler(),
                new ByteToPacketDecoder(),
                new PacketToByteEncoder(),
                new PacketHandlerMapping(handlerMap)
        );
    }
}
