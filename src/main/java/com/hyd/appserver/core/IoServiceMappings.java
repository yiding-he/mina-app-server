package com.hyd.appserver.core;

import com.hyd.appserver.MinaAppServer;

import java.util.HashMap;
import java.util.Map;

/**
 * (描述)
 *
 * @author HeYiding
 */
public class IoServiceMappings {

    private static final Map<Integer, MinaAppServer> MAPPINGS = new HashMap<Integer, MinaAppServer>();

    public static void addMappings(int ioServiceHash, MinaAppServer server) {
        MAPPINGS.put(ioServiceHash, server);
    }

    public static MinaAppServer getServer(int ioServiceHash) {
        return MAPPINGS.get(ioServiceHash);
    }
}
