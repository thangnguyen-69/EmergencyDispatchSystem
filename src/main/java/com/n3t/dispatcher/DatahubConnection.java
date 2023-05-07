package com.n3t.dispatcher;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.n3t.dispatcher.domain.Emergency;
import com.n3t.dispatcher.domain.GeoLocation;
import com.n3t.dispatcher.repository.EmergencyRepository;
import com.n3t.dispatcher.service.AmbulanceService;

import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const;
import wisepaas.datahub.java.sdk.common.Const.EdgeType;
import wisepaas.datahub.java.sdk.common.EdgeAgentListener;
import wisepaas.datahub.java.sdk.common.Enum.ConnectType;
import wisepaas.datahub.java.sdk.model.edge.ConfigAck;
import wisepaas.datahub.java.sdk.model.edge.EdgeAgentOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig;
import wisepaas.datahub.java.sdk.model.edge.TimeSyncCommand;
import wisepaas.datahub.java.sdk.model.edge.WriteValueCommand;
import wisepaas.datahub.java.sdk.model.event.DisconnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.EdgeAgentConnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.MessageReceivedEventArgs;

/**
 * DatahubConnection
 */
@Configuration
public class DatahubConnection {

    @Bean
    public static EdgeAgent createEdgeConnection() throws InterruptedException {
        EdgeAgentOptions options = new EdgeAgentOptions();
        options.NodeId = "7f218380-6a34-45ea-ac20-20792334f69a";    // Obtain from portal
        options.UseSecure = true;
        options.AutoReconnect = true;
        options.ConnectType = ConnectType.DCCS;
        options.Type = EdgeType.Gateway;                // Configure the edge as a Gateway or Device. The default setting is Gateway.
        options.DCCS.CredentialKey = "3a1bf5c4a8483ce1e9625d4d22d6a9j2"; // to lazy to put it elsewhere
        options.DCCS.APIUrl = "https://api-dccs-ensaas.education.wise-paas.com/";
        options.Heartbeat = 60000;                    // The default is 60 seconds.
        options.DataRecover = true;                    // Whether to recover data when disconnected


        EdgeAgentListener agentListener = new EdgeAgentListener() {
            @Autowired
            private AmbulanceService ambulanceService;
            private EmergencyRepository emergencyRepository;

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
                switch (e.Type) {
                    case Const.MessageType.WriteValue:
                        WriteValueCommand wvcMsg = (WriteValueCommand) e.Message;
                        for (WriteValueCommand.Device device : wvcMsg.DeviceList) {
                            System.out.println("AmbulanceId: " + device.Id);
                            String[] subId = device.Id.split(" ");
                            String entityType = subId[0];
                            long id = Long.parseLong(subId[1]);
                            double latitude = -1;
                            double longitude = -1;
                            System.out.printf("entity type: %s, id: %s\n", entityType, id);

                            for (WriteValueCommand.Tag tag : device.TagList) {
                                System.out.printf("TagName: %s, Value: %s\n", tag.Name, tag.Value.toString());
                                if (tag.Name.equals("latitude")) {
                                    latitude = (double) tag.Value;
                                } else if (tag.Name.equals("longitude")) {
                                    longitude = (double) tag.Value;
                                }
                            }
                            if (entityType.equals("ambulance")) {
                                // update ambulance
                                this.ambulanceService.updateAmbulanceLocation(id, latitude, longitude);
                            } else if (entityType.equals("user")) {
                                Optional<Emergency> emg = this.emergencyRepository.findEmergencyByUserId(id);
                                if (!emg.isEmpty()) {
                                    Emergency realEmg = emg.get();
                                    realEmg.setPatientLocation(GeoLocation.fromLatLngToGeometryPoint(latitude, longitude));
                                    this.emergencyRepository.save(realEmg);
                                    continue;
                                }
                                // TODO: what to do if user push location if he is not in any emergency?
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
        edgeAgent.Options = options;
        edgeAgent.Connect();
        while (!edgeAgent.IsConnected()) {
            Thread.sleep(5);
        }
        return edgeAgent;
    }

    public static void registerAmbulance(EdgeAgent edgeAgent, long ambulanceId) {
        EdgeConfig config = new EdgeConfig();

        config.Node = new EdgeConfig.NodeConfig();

        config.Node.DeviceList = new ArrayList<>();

        EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();

        device.Id = "ambulance " + ambulanceId;
        device.Name = device.Id;
        device.Type = "Ambulance";
        device.Description = "Ambulance";


        device.AnalogTagList = new ArrayList<>();
        EdgeConfig.AnalogTagConfig latitudeTag = new EdgeConfig.AnalogTagConfig();
        {
            latitudeTag.Name = "latitude";
            latitudeTag.Description = "latitude of the ambulance";
            latitudeTag.ReadOnly = false;
            latitudeTag.ArraySize = 0;
            latitudeTag.SpanHigh = 90.0;
            latitudeTag.SpanLow = -90.0;
            latitudeTag.EngineerUnit = "°";
            latitudeTag.IntegerDisplayFormat = 3;
            latitudeTag.FractionDisplayFormat = 7;
        }
        device.AnalogTagList.add(latitudeTag);
        EdgeConfig.AnalogTagConfig longitudeTag = new EdgeConfig.AnalogTagConfig();
        {
            longitudeTag.Name = "longitude";
            longitudeTag.Description = "longitude of the ambulance";
            longitudeTag.ReadOnly = false;
            longitudeTag.ArraySize = 0;
            longitudeTag.SpanHigh = 180.0;
            longitudeTag.SpanLow = -180.0;
            longitudeTag.EngineerUnit = "°";
            longitudeTag.IntegerDisplayFormat = 3;
            longitudeTag.FractionDisplayFormat = 7;
            device.AnalogTagList.add(longitudeTag);
        }
        config.Node.DeviceList.add(device);

        Boolean uploadOk = edgeAgent.UploadConfig(Const.ActionType.Create, config);
    }

    public static void registerUser(EdgeAgent edgeAgent, long userId) {
        EdgeConfig config = new EdgeConfig();

        config.Node = new EdgeConfig.NodeConfig();

        config.Node.DeviceList = new ArrayList<>();

        EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();

        device.Id = "user " + userId;
        device.Name = device.Id;
        device.Type = "user";
        device.Description = "user";


        device.AnalogTagList = new ArrayList<>();
        EdgeConfig.AnalogTagConfig latitudeTag = new EdgeConfig.AnalogTagConfig();
        {
            latitudeTag.Name = "latitude";
            latitudeTag.Description = "latitude of the user";
            latitudeTag.ReadOnly = false;
            latitudeTag.ArraySize = 0;
            latitudeTag.SpanHigh = 90.0;
            latitudeTag.SpanLow = -90.0;
            latitudeTag.EngineerUnit = "°";
            latitudeTag.IntegerDisplayFormat = 3;
            latitudeTag.FractionDisplayFormat = 7;
        }
        device.AnalogTagList.add(latitudeTag);
        EdgeConfig.AnalogTagConfig longitudeTag = new EdgeConfig.AnalogTagConfig();
        {
            longitudeTag.Name = "longitude";
            longitudeTag.Description = "longitude of the user";
            longitudeTag.ReadOnly = false;
            longitudeTag.ArraySize = 0;
            longitudeTag.SpanHigh = 180.0;
            longitudeTag.SpanLow = -180.0;
            longitudeTag.EngineerUnit = "°";
            longitudeTag.IntegerDisplayFormat = 3;
            longitudeTag.FractionDisplayFormat = 7;
            device.AnalogTagList.add(longitudeTag);
        }
        config.Node.DeviceList.add(device);

        Boolean uploadOk = edgeAgent.UploadConfig(Const.ActionType.Create, config);
    }
}

