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
package mInternauta.Nermis.Web;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Persistence.nServiceHelper;
import mInternauta.Nermis.Persistence.nStorage;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceState;
import mInternauta.Nermis.Core.nServiceStateRecord;
import mInternauta.Nermis.Core.nServiceStateTable;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Implements the Handler for the Services Page
 */
public class nServicePageHandler extends AbstractHandler {
    private final nWebTemplateManager templates = new nWebTemplateManager();
    
    @Override
    public void handle(String string, Request rqst, HttpServletRequest hsr, HttpServletResponse hsr1) throws IOException, ServletException {        
        StringWriter rpWriter = buildItemContents();
        
        String pageTemplate = templates.load("ServiceStatePage");
        pageTemplate = pageTemplate.replace("{PAGE_CONTENTS}", rpWriter.toString());
        pageTemplate = pageTemplate.replace("{PAGE_TITLE}", nConfigHelper.getDisplayLanguage().getProperty("SERVICEPAGE_TITLE"));
        
        hsr1.setContentType("text/html; charset=utf-8");
        hsr1.setStatus(HttpServletResponse.SC_OK);
        hsr1.getWriter().println(pageTemplate);
        rqst.setHandled(true);
    }

    private StringWriter buildItemContents() {
        nStorage storage =  nStorage.getInstance();
        StringWriter rpWriter = new StringWriter();
        String tplServiceItem = templates.load("ServiceItem");
        if(tplServiceItem.isEmpty() == false) {   
            nServiceStateTable states = storage.loadStates();
            
            for(Entry<String,nServiceStateRecord> entry : states.entrySet()) {
                nService service = nServiceHelper.GetService(entry.getKey());
                nServiceStateRecord record = entry.getValue();
                
                if(service != null) {
                    String item = buildItemHtml(tplServiceItem, service, record);                    
                    rpWriter.append(item);
                }
            }
        } else {
            rpWriter.write(nConfigHelper.getDisplayLanguage().getProperty("REQUEST_FAILED"));
        }
        return rpWriter;
    }

    private String buildItemHtml(String tplServiceItem, nService service, nServiceStateRecord record) {
        // -
        String item = tplServiceItem.trim();
        String description = service.Description;
        
        if(service.RefUrl != null && service.RefUrl.isEmpty() == false) {
            description = "<a href=\"" + service.RefUrl + "\" target=\"_blank\">" + description + "</a>";
        }
        
        item = item.replace("{SERVICE_DESCRIPTION}", description);
        // -
        String stateClass = "";
        String stateMsg = "";
        if(record.State == nServiceState.ONLINE) {
            stateClass = "ONLINE";
            stateMsg = nConfigHelper.getDisplayLanguage().getProperty("SERVICE_ONLINE");
        } else {
            stateClass = "OFFLINE";
            stateMsg = nConfigHelper.getDisplayLanguage().getProperty("SERVICE_OFFLINE");
        }
        item = item.replace("{STATE_CLASS}", stateClass);
        item = item.replace("{STATE}", stateMsg);
        // -
        String updatedAt = nConfigHelper.getDisplayLanguage().getProperty("SERVICE_UPDATEDAT") + " ";
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, Locale.getDefault());
        updatedAt += format.format(record.UpdatedAt);
        item = item.replace("{UPDATED_AT}", updatedAt);
        return item;
    }
    
}
