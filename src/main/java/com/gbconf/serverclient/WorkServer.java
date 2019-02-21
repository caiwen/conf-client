package com.gbconf.serverclient;

import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import com.alibaba.fastjson.JSON;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class WorkServer {

    private ZkClient zkClient;
    private String configPath;
    private String serversPath;
    private ServerData serverData;
    private String configs;
    private IZkDataListener dataListener;
    private String localConfigStoragePath;

    public WorkServer(String configPath, String serversPath,
                      ServerData serverData, ZkClient zkClient, String localConfigStoragePath) {
        this.zkClient = zkClient;
        this.serversPath = serversPath;
        this.configPath = configPath;
        this.serverData = serverData;
        this.localConfigStoragePath = localConfigStoragePath;

        this.dataListener = new IZkDataListener() {
            public void handleDataDeleted(String dataPath) throws Exception {
                // TODO Auto-generated method stub
            }

            public void handleDataChange(String dataPath, Object data)
                    throws Exception {
                // TODO Auto-generated method stub
                configs = new String((byte[]) data);
                updateConfig();
                System.out.println("new Work server config is:" + configs);

            }
        };

    }

    public void start() {
        System.out.println("work server start...");
        initRunning();

    }

    public void stop() {
        System.out.println("work server stop...");
        zkClient.unsubscribeDataChanges(configPath, dataListener);
    }

    private void initRunning() {

        registMe();
        zkClient.subscribeDataChanges(configPath, dataListener);

    }

    private void registMe() {
        String mePath = serversPath.concat("/").concat(serverData.getAddress());

        try {
            zkClient.createEphemeral(mePath, JSON.toJSONString(serverData)
                    .getBytes());
        } catch (ZkNoNodeException e) {
            zkClient.createPersistent(serversPath, true);
            registMe();
        }
    }

    private void updateConfig() {
        String filename = localConfigStoragePath + File.separator + "default.ini";
        Map configsMaps = JSONObject.parseObject(configs, Map.class);
        try {
            File confLocalFile = new File(filename);
            if (!confLocalFile.exists()) {
                boolean creRet = confLocalFile.createNewFile();
            }
            Wini ini = new Wini(confLocalFile);
            for (Object key : configsMaps.keySet()) {
                ini.put("default", (String) key, configsMaps.get(key));
            }
            ini.store();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("success update local configs the new configs is:" + configs);
    }

}

