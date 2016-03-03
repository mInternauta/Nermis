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

import java.io.IOException;
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
import org.apache.commons.net.ftp.FTPClient;

/**
 * Analyzes a FTP service is online and operational.
 * <p>
 * Extended Properties:
 * Hostname => Ftp Server Hostname (can be a IP)
 * Username => Username for authentication (can be anonymous)
 * Password => Password for authentication (can be blank for anonymous account)
 * Port (Default: 21) => Ftp Server port 
 */
public class nFtpWatcher extends nServiceWatcher {

    @Override
    public String getName() {
        return "Ftp";
    }

    @Override
    public nServiceResults execute(nService service, nServiceWatcherContext context) {
        nServiceResults result = new nServiceResults();
        
        String hostname = service.Properties.get("Hostname");
        String user = service.Properties.get("Username");
        String passwd = service.Properties.get("Password");
        String plainPort = service.Properties.getOrDefault("Port", "21");
        int port = Integer.valueOf(plainPort);
        
        FTPClient ftp = new FTPClient();
        
        try {
            
            
            // -------
            context.beginMeasure();            

            // Try to connect
            ftp.connect(hostname, port);
            
            context.stopMeasure("connect");
            // -------
                        
            // -------
            context.beginMeasure();
            // Try to login
            if(ftp.login(user, passwd)) {
                result.State = nServiceState.ONLINE;
            } else {
                result.Message = "Invalid login!";
                result.State = nServiceState.OFFLINE;
            }
            context.stopMeasure("response");
            // -------
            
            // Disconnect
            ftp.disconnect();
        } catch (IOException ex) {
            result.Message = ex.toString();
            result.State = nServiceState.CANT_EXECUTE;
        }
        
        return result;
    }

    @Override
    public boolean validate(nService service) {
        boolean isValid = false;
        
        if(service != null && service.Properties != null) {
            isValid = service.Properties.containsKey("Hostname") &&
                    service.Properties.containsKey("Username") &&
                     service.Properties.containsKey("Password");
        }
        
        return isValid;
    }

    @Override
    public HashMap<String, String> getExtPropertiesHelp() {
        HashMap<String, String> props = new HashMap<>();
        
        props.put("Hostname", "Ftp Server Hostname");
        props.put("Username", "Ftp Server Username, can be anonymous");
        props.put("Password", "Ftp Server Password, can be blank for anonymous");
        props.put("Port", "(Optional)Ftp Server Port, if is blank the watcher will use the default: 21");
        
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
        
        // - Deep Watcher Connection Time
        nStatsDatasource srcConnectionCounter = new nStatsDatasource();
        srcConnectionCounter.Heartbeat = 600;
        srcConnectionCounter.MaxValue = Double.MAX_VALUE;
        srcConnectionCounter.MinValue = 0;
        srcConnectionCounter.Name = nConfigHelper.getDisplayLanguage().getProperty("DS_CONNECT");
        srcConnectionCounter.InternalName = "connect";
        srcConnectionCounter.Type = nStatsDataType.DERIVE;
        
        sources.add(srcResponseTime);
        sources.add(srcConnectionCounter);
        
        return sources;
    }
    
}
