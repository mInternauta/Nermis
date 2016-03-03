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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Core.nStatsDatasource;
import mInternauta.Nermis.Core.nStatsDataType;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nServiceResults;
import mInternauta.Nermis.Core.nServiceState;
import mInternauta.Nermis.Core.nServiceWatcher;
import mInternauta.Nermis.Core.nServiceWatcherContext;
import mInternauta.Nermis.Utils.nResourceHelper;
import org.apache.commons.io.IOUtils;

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
public class nWebWatcher extends nServiceWatcher {

    @Override
    public String getName() {
        return "Web";
    }

    @Override
    public nServiceResults execute(nService service, nServiceWatcherContext context) {
        nServiceResults results = new nServiceResults();
        
        try {
            String method = (String)service.Properties.get("Method");
            String plainUrl = service.Properties.get("Protocol") + "://" + service.Properties.get("Url");
            URL url = new URL(plainUrl);
            
            context.beginMeasure();
            URLConnection conn = url.openConnection();
            context.stopMeasure("connect");
            
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            
            httpConn.setRequestProperty("User-Agent", "Nermis WebWatcher");
            
            if(method.equalsIgnoreCase("Post")) // Send the post data
            {
                context.beginMeasure();
                httpConn.setRequestMethod("POST");
                conn.setDoOutput(true);
                
                // Send the data 
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes((String)service.Properties.get("PostData"));
		wr.flush();
		wr.close();
                

                checkResponse(httpConn, results);
                
                context.stopMeasure("response");
                              
                // Download the Response                
                if(results.State == nServiceState.ONLINE) {
                    downloadResp(httpConn, service, context);                
                }
            } 
            else if(method.equalsIgnoreCase("Get")) // Try fetch the response
            {
                context.beginMeasure();
                httpConn.setRequestMethod("GET");       
                
                checkResponse(httpConn, results);
                context.stopMeasure("response");
                
                if(results.State == nServiceState.ONLINE) {
                    downloadResp(httpConn, service, context);                
                }
            }
            else 
            {
                results.Message = "Unsupported web method: " + method;
                results.State = nServiceState.CANT_EXECUTE;
            }
        } catch (MalformedURLException ex) {
            results.Message = ex.toString();
            results.State = nServiceState.CANT_EXECUTE;
        } catch (IOException ex) {            
            results.Message = ex.toString();
            results.State = nServiceState.OFFLINE;
        } catch (Exception ex) {
            results.Message = ex.toString();
            results.State = nServiceState.OFFLINE;
        }
        
        return results;
    }

    private void downloadResp(HttpURLConnection httpConn, nService service, nServiceWatcherContext context) throws IOException, Exception {
        // Download the page
        String tmpWebDFile = "tempWebD_" + String.valueOf(new Date().getTime()) + ".tmp";
        File tmpFile = nResourceHelper.BuildName("Temp",tmpWebDFile);
        OutputStream tmpDownloadFile = new FileOutputStream(tmpFile);
        InputStream inputResp = httpConn.getInputStream();
        
        context.beginMeasure();
        IOUtils.copy(inputResp, tmpDownloadFile);
        context.stopMeasure("download");
        
        tmpDownloadFile.flush();
        tmpDownloadFile.close();
        
        tmpFile.delete();
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

    @Override
    public ArrayList<nStatsDatasource> getStatsDatasources() {
         ArrayList<nStatsDatasource> sources = new ArrayList<>();
        
        // - Watcher Response Time
        nStatsDatasource srcResponseTime = new nStatsDatasource();
        srcResponseTime.Heartbeat = 600;
        srcResponseTime.MaxValue = Double.MAX_VALUE;
        srcResponseTime.MinValue = 0;
        srcResponseTime.Name = nConfigHelper.getDisplayLanguage().getProperty("DS_RESPONSE");
        srcResponseTime.InternalName = "response";
        srcResponseTime.Type = nStatsDataType.DERIVE;
        
        // - Watcher Connection Time
        nStatsDatasource srcConnectionCounter = new nStatsDatasource();
        srcConnectionCounter.Heartbeat = 600;
        srcConnectionCounter.MaxValue = Double.MAX_VALUE;
        srcConnectionCounter.MinValue = 0;
        srcConnectionCounter.Name = nConfigHelper.getDisplayLanguage().getProperty("DS_CONNECT");
        srcConnectionCounter.InternalName = "connect";
        srcConnectionCounter.Type = nStatsDataType.DERIVE;
        
        // - Watcher Download Time
        nStatsDatasource srcDownloadTime = new nStatsDatasource();
        srcDownloadTime.Heartbeat = 600;
        srcDownloadTime.MaxValue = Double.MAX_VALUE;
        srcDownloadTime.MinValue = 0;
        srcDownloadTime.Name = nConfigHelper.getDisplayLanguage().getProperty("DS_DOWNLOAD");
        srcDownloadTime.InternalName = "download";
        srcDownloadTime.Type = nStatsDataType.DERIVE;
        
        sources.add(srcResponseTime);
        sources.add(srcConnectionCounter);
        sources.add(srcDownloadTime);
        
        return sources;
    }
    
}
