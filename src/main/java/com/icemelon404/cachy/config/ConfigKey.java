package com.icemelon404.cachy.config;

public enum ConfigKey {
    SERVER_PORT("server-port"),
    DATA_PATH("data-path"),
    SSL_BLOCK_SIZE("ssl-block-size"),
    SSL_CONCORRENCY("ssl-concurrency"),
    COMPACT_MAX_SIZE("compact-max-size"),
    COMPACT_INTERVAL("compact-interval"),
    MEMTABLE_THRESHOLD("memtable-threshold");

    private String path;

    ConfigKey(String name) {
        this.path = name;
    }

    @Override
    public String toString() {
        return path;
    }
}
