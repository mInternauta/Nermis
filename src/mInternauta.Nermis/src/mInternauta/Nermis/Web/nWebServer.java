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
import mInternauta.Nermis.nController;
import mInternauta.Nermis.nResourceHelper;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

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
            nController.CurrentLogger.log(Level.INFO, "Stopping Nermis Builtin Web Server...");
            if(server != null) {
                server.stop();
            }
        } catch (Exception ex) {
            nController.CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Start the Web Server
     */
    public void Start() {
        try {
            nController.CurrentLogger.log(Level.INFO, "Starting Nermis Builtin Web Server...");
            
            // Create the Web server path if not exists
            this.webServerPath = nResourceHelper.BuildDirectory("Web");
            
            // Copy all embedded resources to the WebServer Path
            nResourceHelper.CopyEmbedded("/assets/Nermis/Web/nermis.css", this.webServerPath + "/nermis.css");
            nResourceHelper.CopyEmbedded("/assets/Nermis/Web/Icon.png", this.webServerPath + "/Icon.png");
            nResourceHelper.CopyEmbedded("/assets/Nermis/Web/index.html", this.webServerPath + "/index.html");
            
            int webserverPort = nConfigHelper.getConfiguration().WebServerPort;
            
            // Setup the Server
            this.server = new Server(webserverPort);
            
            // - Handlers
            ContextHandler handlers = setupHandlers();            
            this.server.setHandler(handlers);
            //            
            this.server.start();
            nController.CurrentLogger.log(Level.INFO, "Nermis Builtin Web Server started at http://localhost:{0}", String.valueOf(webserverPort));
        } catch (Exception ex) {
            nController.CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }

    private ContextHandler setupHandlers() {        
        ContextHandler ctxServer = new ContextHandler();
        ctxServer.setContextPath("/");
        
        // Setup all the Handlers
        nJettyContextHandlerCollection handlers = new nJettyContextHandlerCollection();
                
        //-
        ContextHandler ctxResources = new ContextHandler("/");
        ResourceHandler resHandler = new ResourceHandler();
        resHandler.setResourceBase(webServerPath);
        resHandler.setDirectoriesListed(true);
        ctxResources.setHandler(resHandler);
        
        // Service Status Page
        ContextHandler servicePage = new ContextHandler();
        servicePage.setContextPath("/status.do");
        servicePage.setHandler(new nServicePageHandler());
        
        //-
        handlers.setHandlers(new Handler[] {ctxResources, servicePage});
        ctxServer.setHandler(handlers);
        
        return ctxServer;
    }


}
