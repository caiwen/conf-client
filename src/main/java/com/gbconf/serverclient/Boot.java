package com.gbconf.serverclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;


public class Boot {
    private static final String COMPUTER_NAME = "COMPUTERNAME";

    public static void main(String[] args) {
        try {
            WorkServer workServer = initWorkServer();
            workServer.start();
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化服务器工作区
     *
     * @return WorkServer
     * @throws Exception
     */
    private static WorkServer initWorkServer() throws Exception {
        InetAddress addr;
        addr = InetAddress.getLocalHost();
        Map<String, String> map = System.getenv();
        String ip = addr.getHostAddress();
        String computerName = map.get(COMPUTER_NAME);
        ServerData serverData = new ServerData();
        serverData.setId(UUID.randomUUID().toString().replace("-", ""));
        serverData.setName(computerName);
        serverData.setAddress(ip);
        String zookeeperServer = PropertiesUtil.getValue("zkServer");
        String configNodePath = PropertiesUtil.getValue("configNodePath");
        String serverInfoNodePath = PropertiesUtil.getValue("serverInfoNodePath");
        String localConfigStoragePath = PropertiesUtil.getValue("localConfigStoragePath");
        ZkClient client = new ZkClient(zookeeperServer, 5000, 5000, new BytesPushThroughSerializer());
        return new WorkServer(configNodePath, serverInfoNodePath, serverData, client, localConfigStoragePath);
    }
}
