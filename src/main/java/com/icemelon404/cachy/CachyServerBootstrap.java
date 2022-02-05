package com.icemelon404.cachy;

import com.icemelon404.cachy.config.CachyConfig;
import com.icemelon404.cachy.config.ConfigKey;
import com.icemelon404.cachy.network.DefaultChannelInitializer;
import com.icemelon404.cachy.network.NettyServerStarter;
import com.icemelon404.cachy.network.get.GetMessageHandlerImpl;
import com.icemelon404.cachy.network.packet.PacketHandler;
import com.icemelon404.cachy.network.packet.get.GetPacketHandler;
import com.icemelon404.cachy.network.packet.set.SetPacketHandler;
import com.icemelon404.cachy.network.set.SetMessageHandlerImpl;

import java.util.Collection;
import java.util.LinkedList;

public class CachyServerBootstrap {

    private int port;

    public CachyServerBootstrap(CachyConfig config) {
        this.port = (int) config.get(ConfigKey.SERVER_PORT);
    }

    public NettyServerStarter getServer(StorageLoadResult result) {
        Collection<PacketHandler> handlers = new LinkedList<>();
        handlers.add(new SetPacketHandler(new SetMessageHandlerImpl(result.writer)));
        handlers.add(new GetPacketHandler(new GetMessageHandlerImpl(result.reader)));
        DefaultChannelInitializer initializer = new DefaultChannelInitializer(handlers);
        return new NettyServerStarter(initializer, port);
    }

}
