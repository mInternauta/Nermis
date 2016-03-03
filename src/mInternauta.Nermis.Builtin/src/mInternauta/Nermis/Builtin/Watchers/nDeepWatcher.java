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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
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

/**
 * This watcher works together with Deep Inspector to examine whether a connection is functional.
 */
public class nDeepWatcher extends nServiceWatcher {

    @Override
    public String getName() {
        return "DeepInspector";
    }
    
    @Override
    public boolean validate(nService service) {
        return service.Properties != null && service.Properties.containsKey("Hostname");
    }

    @Override
    public HashMap<String, String> getExtPropertiesHelp() {
        HashMap<String, String> props = new HashMap<>();
        
        props.put("Hostname", "The hostname to the DeepInspector");
        
        return props;
    }

    private boolean sendCheck(int i, DataOutputStream outToServer, BufferedReader inFromServer) throws IOException {
        outToServer.write(i);
        int recv = inFromServer.read();
        return i == recv;
    }
    
    @Override
    public nServiceResults execute(nService service, nServiceWatcherContext context) {
        nServiceResults results = new nServiceResults();
        results.State = nServiceState.CANT_EXECUTE;

        try {
            String hostname = service.Properties.get("Hostname");  

            // -
            context.beginMeasure();
            Socket socket = new Socket(hostname, 5050);
            
            // -
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());  
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));            
            
            context.stopMeasure("connect");
            
            // - Send data to the server
            context.beginMeasure();
            
            boolean passed = sendCheck(14, outToServer, inFromServer);
            passed = passed && sendCheck(16, outToServer, inFromServer);
            passed = passed && sendCheck(16, outToServer, inFromServer);
            passed = passed && sendCheck(14, outToServer, inFromServer);
                       
            
            context.stopMeasure("response");
            
            // - 
            if(passed) {
                results.State = nServiceState.ONLINE;
            }
            else 
            {
                results.State = nServiceState.OFFLINE;
                results.Message = "Inconsistent or unstable transfer.";
            }
            
            socket.close();
        } catch (IOException ex) {
            results.State = nServiceState.ERROR;
            results.Message = ex.getMessage();
        }
        
        return results;
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
