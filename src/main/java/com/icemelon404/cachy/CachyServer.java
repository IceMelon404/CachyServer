package com.icemelon404.cachy;

import com.icemelon404.cachy.config.CachyConfig;
import com.icemelon404.cachy.config.ConfigMock;

import java.util.concurrent.ExecutionException;

public class CachyServer {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CachyConfig config = new ConfigMock();
        CachyStorageBootstrap storageBootstrap = new CachyStorageBootstrap(config);
        CachyServerBootstrap server = new CachyServerBootstrap(config);
        server.getServer(storageBootstrap.runStorage());
    }
}
