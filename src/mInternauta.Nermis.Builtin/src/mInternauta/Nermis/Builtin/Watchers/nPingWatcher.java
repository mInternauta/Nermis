/*
 * Copyright (C) 2015 mInternauta
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package mInternauta.Nermis.Builtin.Watchers;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceResults;
import mInternauta.Nermis.Core.nServiceState;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Core.nServiceWatcherContext;
import mInternauta.Nermis.Core.nStatsDataType;
import mInternauta.Nermis.Core.nStatsDatasource;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;
import org.xbill.DNS.TextParseException;

/**
 * This watcher analyzes a hostname and ping it 
 */
public class nPingWatcher  extends nServiceWatcher {

    @Override
    public String getName() {
        return "Ping";
    }

    @Override
    public nServiceResults execute(nService service, nServiceWatcherContext context) {
        nServiceResults result = new nServiceResults();
        String hostname = service.Properties.get("Hostname");

        if (hostname != null && hostname.isEmpty() == false) {
            try 
            {
                int count = 10;
                double totalTime = 0;
                int successPingCount = 0;
                
                // - Prepare the request
                IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
                request.setHost(hostname);
                
                if(service.Properties.containsKey("Timeout")) {
                    request.setTimeout(Integer.valueOf(service.Properties.get("Timeout")));
                }
                
                if(service.Properties.containsKey("Repeat")) {
                    count = Integer.valueOf(service.Properties.get("Repeat"));
                }
                
                // Start the Ping
                context.Logger.log(Level.INFO, "Starting PING to {0}", hostname);
                
                for(int time = 1; time <= count; time++) {
                    IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
                    
                    totalTime += response.getDuration();
                    
                    if(response.getSuccessFlag()) {
                        successPingCount++;
                    }
                    
                    context.Logger.log(Level.INFO, IcmpPingUtil.formatResponse(response));
                }
                
                // Calculate the Ping
                double medTime = Math.round(totalTime / count);
                
                // Update the statistics
                context.setStatsData("response", medTime);
                context.setStatsData("overall", totalTime);
                
                if(successPingCount == count) {
                    result.State = nServiceState.ONLINE;                    
                } 
                else if(successPingCount > 0 && successPingCount < count)
                {
                    result.State = nServiceState.ONLINE;                    
                    result.Message = "One or more tests obtained miscommunication.";
                }
                else 
                {
                    result.State = nServiceState.OFFLINE;                    
                    result.Message = "The host didnt respond!";
                }
            } 
            catch (Exception ex) 
            {
                context.Logger.log(Level.SEVERE, null, ex);
                result.Message = ex.getMessage();
                result.State = nServiceState.CANT_EXECUTE;
            }
        } else {
            result.Message = "Invalid hostname";
            result.State = nServiceState.CANT_EXECUTE;
        }
        
        return result;
    }

    @Override
    public boolean validate(nService service) {
           boolean isValid = false;

        if (service != null && service.Properties != null) {
            isValid = service.Properties.containsKey("Hostname");
        }

        return isValid;
    }

    @Override
    public HashMap<String, String> getExtPropertiesHelp() {
           HashMap<String, String> props = new HashMap<>();

        props.put("Hostname", "The Hostname or IP to ping");
        props.put("Timeout", "Timeout (ms) for the ICMP Response (Default: 500)");
        props.put("Repeat", "How much times the ping will be executed");

        return props;
    }

    @Override
    public ArrayList<nStatsDatasource> getStatsDatasources() {
        ArrayList<nStatsDatasource> sources = new ArrayList<>();

        // - Ping Response Time
        nStatsDatasource srcResponseTime = new nStatsDatasource();
        srcResponseTime.Heartbeat = 600;
        srcResponseTime.MaxValue = Double.MAX_VALUE;
        srcResponseTime.MinValue = 0;
        srcResponseTime.Name = nConfigHelper.getDisplayLanguage().getProperty("DS_RESPONSE");
        srcResponseTime.InternalName = "response";
        srcResponseTime.Type = nStatsDataType.DERIVE;
        
          // - Ping overall Time
        nStatsDatasource srcProcessTime = new nStatsDatasource();
        srcProcessTime.Heartbeat = 600;
        srcProcessTime.MaxValue = Double.MAX_VALUE;
        srcProcessTime.MinValue = 0;
        srcProcessTime.Name = nConfigHelper.getDisplayLanguage().getProperty("DS_OVERALL");
        srcProcessTime.InternalName = "overall";
        srcProcessTime.Type = nStatsDataType.DERIVE;

        sources.add(srcResponseTime);

        return sources;
    }
    
}
