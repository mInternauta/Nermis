/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mInternauta.Nermis.Builtin.Watchers;

import java.util.ArrayList;
import java.util.HashMap;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Core.nStatsDatasource;
import mInternauta.Nermis.Core.nStatsDataType;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceResults;
import mInternauta.Nermis.Core.nServiceState;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Core.nServiceWatcherContext;
import mInternauta.Nermis.Net.SocketUtils;

/**
 *  Analyzes a TCP or UDP service if is online and operational.
 * <p>
 * Extended Properties:
 * Hostname => Server Hostname (can be a IP)
 * Port => Server port 
 * Protocol => TCP , UDP
 */
public class nPortWatcher extends nServiceWatcher {

    @Override
    public String getName() {
        return "Port";
    }

    @Override
    public nServiceResults execute(nService service, nServiceWatcherContext context) {
        nServiceResults result = new nServiceResults();
        String hostname = service.Properties.get("Hostname");
        String protocol = service.Properties.get("Protocol");
        String plainPort = service.Properties.getOrDefault("Port", "1");
        int port = Integer.valueOf(plainPort);
        
        context.beginMeasure();
        
       if(protocol.equalsIgnoreCase("UDP")) {
           if(SocketUtils.checkUdpPort(hostname, port)) {
               result.State = nServiceState.ONLINE;
           }
           else 
           {
               result.State = nServiceState.OFFLINE;
               if(SocketUtils.LastException != null) 
               {
                    result.Message = SocketUtils.LastException.toString();
               }
           }
        }        
        else 
        {
          if(SocketUtils.checkTcpPort(hostname, port)) {
               result.State = nServiceState.ONLINE;
           }
           else 
           {
               result.State = nServiceState.OFFLINE;
               if(SocketUtils.LastException != null) 
               {
                    result.Message = SocketUtils.LastException.toString();
               }
           }
        }
       
       context.stopMeasure("response");
       
        return result;
    }

    @Override
    public boolean validate(nService service) {
            boolean isValid = false;
        
        if(service != null && service.Properties != null) {
            isValid = service.Properties.containsKey("Hostname") &&
                    service.Properties.containsKey("Protocol") &&
                     service.Properties.containsKey("Port");
        }
        
        return isValid;
    }

    @Override
    public HashMap<String, String> getExtPropertiesHelp() {
             HashMap<String, String> props = new HashMap<>();
        
        props.put("Hostname", "Server Hostname");
        props.put("Port", "Server Port");
        props.put("Protocol", "Server Port Protocol (TCP or UDP)");
        
        return props;
    }

    @Override
    public ArrayList<nStatsDatasource> getStatsDatasources() {
        ArrayList<nStatsDatasource> sources = new ArrayList<>();
        
        // - Deep Watcher Response Time
        nStatsDatasource srcResponseTime = new nStatsDatasource();
        srcResponseTime.Heartbeat = 600;
        srcResponseTime.MaxValue = Double.MAX_VALUE;
        srcResponseTime.MinValue = 0;
        srcResponseTime.Name = nConfigHelper.getDisplayLanguage().getProperty("DS_RESPONSE");
        srcResponseTime.InternalName = "response";
        srcResponseTime.Type = nStatsDataType.DERIVE;

        sources.add(srcResponseTime);
        
        return sources;
    }
    
}
