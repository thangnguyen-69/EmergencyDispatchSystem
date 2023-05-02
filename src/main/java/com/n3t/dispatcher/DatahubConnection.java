package com.n3t.dispatcher;

import java.util.Date;

import org.springframework.context.annotation.Configuration;

import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const;
import wisepaas.datahub.java.sdk.common.Const.EdgeType;
import wisepaas.datahub.java.sdk.common.EdgeAgentListener;
import wisepaas.datahub.java.sdk.common.Enum.ConnectType;
import wisepaas.datahub.java.sdk.common.Enum.Protocol;
import wisepaas.datahub.java.sdk.model.edge.ConfigAck;
import wisepaas.datahub.java.sdk.model.edge.DCCSOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeAgentOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeData;
import wisepaas.datahub.java.sdk.model.edge.MQTTOptions;
import wisepaas.datahub.java.sdk.model.edge.TimeSyncCommand;
import wisepaas.datahub.java.sdk.model.edge.WriteValueCommand;
import wisepaas.datahub.java.sdk.model.edge.EdgeData.Tag;
import wisepaas.datahub.java.sdk.model.event.DisconnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.EdgeAgentConnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.MessageReceivedEventArgs;

/**
 * DatahubConnection
 */
@Configuration
public class DatahubConnection {

    public EdgeAgent createEdgeConnection(){
        EdgeAgentOptions options = new EdgeAgentOptions();

        options.ConnectType = ConnectType.DCCS;				// Connection type (DCCS, MQTT). The default setting is DCCS.
        // If connectType is DCCS, the following options must be input:
        options.DCCS = new DCCSOptions("89489f09de05c02746bf3421d94506bw", "https://api-dccs-ensaas.education.wise-paas.com/");
        
        options.UseSecure = true;
        options.AutoReconnect = true;
        options.NodeId = "78b51e16-0846-4fd1-9bdb-ac4807cbbb60"; 	// Obtain from portal
        options.Type = EdgeType.Gateway;				// Configure the edge as a Gateway or Device. The default setting is Gateway.
        options.DeviceId = "SmartDevice1";				// If the Type is Device, the DeviceID must be input.
        options.Heartbeat = 60000;					// The default is 60 seconds.
        options.DataRecover = true;					// Whether to recover data when disconnected
        
        EdgeAgentListener agentListener = new EdgeAgentListener() {
            @Override
            public void Connected(EdgeAgent agent, EdgeAgentConnectedEventArgs args) {
                System.out.println("Connected");
            }
        
            @Override
            public void Disconnected(EdgeAgent agent, DisconnectedEventArgs args) {
                System.out.println("Disconnected");
            }
        
            @Override
            public void MessageReceived(EdgeAgent agent, MessageReceivedEventArgs e) {
                System.out.println("MessageReceived");
                switch (e.Type) {
                    case Const.MessageType.WriteValue:
                        WriteValueCommand wvcMsg = (WriteValueCommand) e.Message;
                        for (WriteValueCommand.Device device: wvcMsg.DeviceList) {
                            System.out.println("DeviceID:" + device.Id);
                            for (WriteValueCommand.Tag tag: device.TagList) {
                                System.out.printf("TagName: %s, Value: %s\n", tag.Name, tag.Value.toString());
                            }
                        }
                        break;
                    case Const.MessageType.TimeSync:
                        TimeSyncCommand tscMsg = (TimeSyncCommand) e.Message;
                        System.out.println("UTC Time:" + tscMsg.UTCTime.toString());
                        break;
                    case Const.MessageType.ConfigAck:
                        ConfigAck cfgAckMsg = (ConfigAck) e.Message;
                        String result = cfgAckMsg.Result.toString();
                        break;
                }
            }
        };
        EdgeAgent edgeAgent = new EdgeAgent(options, agentListener);
        EdgeData data = new EdgeData();
        data.Timestamp = new Date();
        EdgeData.Tag aTag = new Tag();
        aTag.DeviceId = "1";
        aTag.TagName = "latitude";
        aTag.Value = 10.04;
        data.TagList.add(aTag);
        edgeAgent.SendData(data);

        EdgeConfig config = new EdgeConfig();
        EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();
        device.Id = "ambulance2";
        device.Name = "device 2";
        device.Type = "Smart Device 2";
        device.Description = "Device2";
        config.Node.DeviceList.add(device);
        edgeAgent.UploadConfig(Const.ActionType.Create, config);
        return edgeAgent;
    
    }
}

