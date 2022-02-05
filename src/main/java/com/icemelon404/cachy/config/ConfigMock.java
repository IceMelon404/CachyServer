package com.icemelon404.cachy.config;

import com.icemelon404.cachy.config.CachyConfig;
import com.icemelon404.cachy.config.ConfigKey;

public class ConfigMock implements CachyConfig {

    @Override
    public Object get(ConfigKey key) {
        switch (key) {
            case SERVER_PORT:
                return 8080;
            case DATA_PATH:
                return "/home/junsekim/cachy";
            case SSL_BLOCK_SIZE:
                return 20;
            case MEMTABLE_THRESHOLD:
                return 100000;
            case COMPACT_INTERVAL:
                return 5000L;
            case SSL_CONCORRENCY:
                return 10;
            case COMPACT_MAX_SIZE:
                return 5;
            default:
                return null;
        }
    }
}
