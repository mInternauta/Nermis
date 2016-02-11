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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceResults;
import mInternauta.Nermis.Core.nServiceState;
import mInternauta.Nermis.Core.nServiceWatcher;

/**
 * This watcher analyzes a Web http or https connection, it can analyze the POST or GET request.
 * <p>
 * Extended Properties:
 * Method => POST , GET
 * Protocol => http,https
 * Url => Website url or ip (www.mywebsite.com)
 * If the method is POST you need to store the post data in this property:
 * PostData => The Post data, format like URL Encoding:
 * mykey=myvalue&mykey2=myvalue2
 */
public class nWebWatcher implements nServiceWatcher {

    @Override
    public String getName() {
        return "Web";
    }

    @Override
    public nServiceResults execute(nService service) {
        nServiceResults results = new nServiceResults();
        
        try {
            String method = (String)service.Properties.get("Method");
            String plainUrl = service.Properties.get("Protocol") + "://" + service.Properties.get("Url");
            URL url = new URL(plainUrl);
            URLConnection conn = url.openConnection();
            
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            
            httpConn.setRequestProperty("User-Agent", "Nermis WebWatcher");
            
            if(method.equalsIgnoreCase("Post")) // Send the post data
            {
                httpConn.setRequestMethod("POST");
                conn.setDoOutput(true);
                
                // Send the data 
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes((String)service.Properties.get("PostData"));
		wr.flush();
		wr.close();
                
                checkResponse(httpConn, results);
            } 
            else if(method.equalsIgnoreCase("Get")) // Try fetch the response
            {
                httpConn.setRequestMethod("GET");                
                
                checkResponse(httpConn, results);
            }
            else 
            {
                results.Message = "Unsupported web method: " + method;
                results.State = nServiceState.CANT_EXECUTE;
            }
        } catch (MalformedURLException ex) {
            results.Message = ex.getLocalizedMessage();
            results.State = nServiceState.CANT_EXECUTE;
        } catch (IOException ex) {            
            results.Message = ex.getLocalizedMessage();
            results.State = nServiceState.OFFLINE;
        }
        
        return results;
    }

    private void checkResponse(HttpURLConnection httpConn, nServiceResults results) throws IOException {
        // Try get the response
        if(httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            results.State = nServiceState.ONLINE;
        } else {
            results.State = nServiceState.OFFLINE;
        }
    }

    @Override
    public boolean validate(nService service) {
         boolean isValid = false;
        
        if(service != null && service.Properties != null) {
            isValid = service.Properties.containsKey("Url") && 
                    service.Properties.containsKey("Protocol") && 
                    service.Properties.containsKey("Method");
            
            if(isValid) {
                String method = (String)service.Properties.get("Method");
                if(method.equalsIgnoreCase("Post")) {
                    isValid = service.Properties.containsKey("PostData");
                }
            }
        }
        
        return isValid;
    }

    @Override
    public HashMap<String, String> getExtPropertiesHelp() {
        HashMap<String, String> props = new HashMap<>();
        
        props.put("Method", "POST | GET");
        props.put("Protocol", "http | https");
        props.put("Url", "Website url or ip (www.mywebsite.com)");
        props.put("PostData", "If the method is POST you need to store the post data in this property: \r\n formatted like URL Encoding:\n" +
            "mykey=myvalue&mykey2=myvalue2");
        
        return props;
    }
    
}
