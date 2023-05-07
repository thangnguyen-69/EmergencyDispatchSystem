package com.n3t.demo;

import com.n3t.dispatcher.DatahubConnection;
import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const;
import wisepaas.datahub.java.sdk.common.EdgeAgentListener;
import wisepaas.datahub.java.sdk.common.Enum.ConnectType;
import wisepaas.datahub.java.sdk.model.edge.EdgeAgentOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeData;
import wisepaas.datahub.java.sdk.model.event.DisconnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.EdgeAgentConnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.MessageReceivedEventArgs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.context.ActiveProfiles;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
// https://www.baeldung.com/junit-5-parallel-tests
@Execution(ExecutionMode.CONCURRENT)
@ActiveProfiles("test")
class DataHubTests {


    @Test 
    void testSendData() throws InterruptedException{
        EdgeAgent agent = DatahubConnection.createEdgeConnection();
        // agent.SendData(null);
        System.out.println(agent.IsConnected());
        Random random = new Random();
        EdgeData data = new EdgeData();
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 5; j++) {
                EdgeData.Tag latTag = new EdgeData.Tag(); {
                    latTag.DeviceId = "ambulance" + i;
                    latTag.TagName = "latitude";
                    latTag.Value = random.nextInt(100);
                }
                EdgeData.Tag longTag = new EdgeData.Tag(); {
                    longTag.DeviceId = "ambulance" + i;
                    longTag.TagName = "longitude";
                    longTag.Value = j % 2;
                }


                data.TagList.add(latTag);
                data.TagList.add(longTag);
            }
        }
        data.Timestamp = new Date();
        Boolean result = agent.SendData(data);
        System.out.println("result:"+result);
    }

    @Test 
    void testUpdateAmbulance() throws InterruptedException{
        EdgeAgent agent = DatahubConnection.createEdgeConnection();
        Thread.sleep(20000);
    }

    // @Test
    void datahubWorkOk() throws InterruptedException {
        EdgeAgentOptions options = new EdgeAgentOptions();
        options.NodeId = "7f218380-6a34-45ea-ac20-20792334f69a"; 	// Obtain from portal
        options.UseSecure = true;
        options.ConnectType = ConnectType.DCCS;
        options.DCCS.CredentialKey = "3a1bf5c4a8483ce1e9625d4d22d6a9j2";
        options.DCCS.APIUrl = "https://api-dccs-ensaas.education.wise-paas.com/";
        // options.AndroidPackageName = "com.sth";
        
        EdgeAgentListener agentListener = new EdgeAgentListener() {
            @Override
            public void Connected(EdgeAgent agent, EdgeAgentConnectedEventArgs args) {
                System.out.println("Connected haha goddamnit");
            }
        
            @Override
            public void Disconnected(EdgeAgent agent, DisconnectedEventArgs args) {
                System.out.println("Disconnected");
            }
        
            @Override
            public void MessageReceived(EdgeAgent agent, MessageReceivedEventArgs e) {
                System.out.println("MessageReceived");
            }
        };
        EdgeAgent edgeAgent = new EdgeAgent(options, agentListener);
        edgeAgent.Options = options;
        edgeAgent.Connect();



        Thread.sleep(3000);
        System.out.println("is connected:"+ edgeAgent.IsConnected());

        EdgeConfig config = new EdgeConfig();

        config.Node = new EdgeConfig.NodeConfig();

        config.Node.DeviceList = new ArrayList<>();

            EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();
            
            device.Id = "wtf3";
            device.Name = "bro3";
            device.Type = "Smart shit";
            device.Description = "Device";
            

            device.AnalogTagList = new ArrayList<>();
            device.DiscreteTagList = new ArrayList<>();
            device.TextTagList = new ArrayList<>();

            config.Node.DeviceList.add(device);

        Boolean uploadOk = edgeAgent.UploadConfig(Const.ActionType.Create, config);
        System.out.println("upload success?"+ uploadOk);
    }
   
}