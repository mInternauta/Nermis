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
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * This watcher analyzes a hostname and try to resolve it
 */
public class nDnsWatcher extends nServiceWatcher {

    @Override
    public String getName() {
        return "Dns";
    }

    @Override
    public nServiceResults execute(nService service, nServiceWatcherContext context) {
        nServiceResults result = new nServiceResults();
        String hostname = service.Properties.get("Hostname");

        if (hostname != null && hostname.isEmpty() == false) {
            try {
                Resolver resolver = null;
                
                if(service.Properties.containsKey("DnsServer")) {
                    resolver = new SimpleResolver(service.Properties.get("DnsServer"));
                }
                else 
                {
                    resolver = new SimpleResolver();
                }
                
                Lookup lookup = new Lookup(hostname, Type.A);
                
                lookup.setResolver(resolver);
                
                context.beginMeasure();
                Record[] records = lookup.run();
                context.stopMeasure("response");
                
                if(lookup.getResult() == Lookup.SUCCESSFUL) {
                    result.State = nServiceState.ONLINE;
                } 
                else 
                {
                    result.Message = lookup.getErrorString();
                    result.State = nServiceState.OFFLINE;
                }
            } catch (TextParseException | UnknownHostException ex) {
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

        props.put("Hostname", "The Hostname to Resolve");
        props.put("DnsServer", "The DNS Server to use (Optional) \n If not setted the watcher will use the system primary dns server");

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
