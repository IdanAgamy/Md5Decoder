package com.idan.md5Decoder.controler;

import com.idan.md5Decoder.beans.Server;

import java.util.HashMap;
import java.util.Map;

abstract class ServerController {

    private Server thisServer;
    private Map<Integer, Server> servers;


    public ServerController(Server thisServer) {
        this.thisServer = thisServer;
        this.servers = new HashMap<>();
    }

    public Map<Integer, Server> getServers() {
        return servers;
    }

    public Server getThisServer() {
        return thisServer;
    }
}
