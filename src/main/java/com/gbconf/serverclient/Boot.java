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
            //初始化服务器
            WorkServer workServer = initWorkServer();
            //启动
            workServer.start();
            //保持程序不终端，使zk保持长连接
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
        //获取服务器ip
        String ip = addr.getHostAddress();
        //服务器名称
        String computerName = map.get(COMPUTER_NAME);
        //服务器
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
