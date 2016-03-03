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

import java.util.logging.Level;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Utils.nResourceHelper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;

/**
 * Nermis Built-in Web Server
 */
public class nWebServer {
    private String webServerPath;
    private Server server;
    
    /**
     * Stops the Web Server
     */
    public void Stop() 
    {
        try {
            CurrentLogger.log(Level.INFO, "Stopping Nermis Builtin Web Server...");
            if(server != null) {
                server.stop();
            }
        } catch (Exception ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Start the Web Server
     */
    public void Start() {
        try {
            CurrentLogger.log(Level.INFO, "Starting Nermis Builtin Web Server...");
            
            // Create the Web server path if not exists
            this.webServerPath = nResourceHelper.BuildDirectory("Web");
            
            // Copy all embedded resources to the WebServer Path
            nResourceHelper.CopyEmbedded("/assets/Nermis/Web/nermis.css", this.webServerPath + "/nermis.css");
            nResourceHelper.CopyEmbedded("/assets/Nermis/Web/Icon.png", this.webServerPath + "/icon.png");
            nResourceHelper.CopyEmbedded("/assets/Nermis/Web/index.html", this.webServerPath + "/index.html");
            nResourceHelper.CopyEmbedded("/assets/Nermis/Web/favicon.ico", this.webServerPath + "/favicon.ico");
            
            int webserverPort = nConfigHelper.getConfiguration().WebServerPort;
            
            // Setup the Server
            this.server = new Server(webserverPort);
            
            // - Handlers
            ContextHandler handlers = setupHandlers();            
            this.server.setHandler(handlers);
            //            
            this.server.start();
            CurrentLogger.log(Level.INFO, "Nermis Builtin Web Server started at http://localhost:{0}", String.valueOf(webserverPort));
        } catch (Exception ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }

    private ContextHandler setupHandlers() {        
        ContextHandler ctxServer = new ContextHandler();
        ctxServer.setContextPath("/");
               
        nWebDefaultHandler mainHandler = new nWebDefaultHandler(this.webServerPath);
        mainHandler.addMapping("/status.do", new nServicePageHandler());        
        mainHandler.addMapping("/charts.do", new nServiceChartsHandler());        
        
        ctxServer.setWelcomeFiles(new String[] {"index.html"});
        ctxServer.setHandler(mainHandler);
        
        
        return ctxServer;
    }


}
