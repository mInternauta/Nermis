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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Core.IStatsGraphManager;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Core.nStatsGraphDef;
import mInternauta.Nermis.Persistence.nServiceHelper;
import mInternauta.Nermis.Utils.nTemplateManager;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.MultiMap;

/**
 * Service Graphs Page
 */
public class nServiceChartsHandler extends AbstractHandler {

    private final nTemplateManager templates = new nTemplateManager();
    private nService SelectedService;
    private nStatsGraphDef SelectedGraph;
    private String cSelectedService;
    private String cSelectedGraph;

    @Override
    public void handle(String string, Request rqst, HttpServletRequest hsr, HttpServletResponse hsr1) throws IOException, ServletException {
       this.cSelectedService = "";
       this.cSelectedGraph = "";
        
        checkPost(rqst);

        // - Load the template
        String pageTemplate = loadTemplate();
        pageTemplate = buildServiceList(this.cSelectedService, pageTemplate);

        // - If service is selected, load the graphs list 
        if (SelectedService != null) {
            pageTemplate = buildGraphList(this.cSelectedGraph, pageTemplate);
        }
        
        // - If a graph is selected, load the image
        if(SelectedGraph != null) {
            File fGraph = new File(SelectedGraph.GraphFile);
            if(fGraph.exists()) {
                String imageBase64 = nWebUtils.ImageToBase64(fGraph);
                pageTemplate = pageTemplate.replace("{IMG_SOURCE}", "data:image/png;base64," + imageBase64);           
            }
        }

        hsr1.setContentType("text/html; charset=utf-8");
        hsr1.setStatus(HttpServletResponse.SC_OK);
        hsr1.getWriter().println(pageTemplate);
        rqst.setHandled(true);
    }

    private String buildGraphList(String selectedGraph, String pageTemplate) {
        StringBuilder sbGraphsList = new StringBuilder();
        ArrayList<nStatsGraphDef> graphs = IStatsGraphManager.getAllGraphs(SelectedService);
        for (nStatsGraphDef graph : graphs) {
            
            String selected = "";
            
            if (graph.Name.equalsIgnoreCase(selectedGraph)) {
                selected = "selected";
                this.SelectedGraph = graph;
            }
            sbGraphsList.append("<option value=\"" + graph.Name + "\"" + selected + " >" + graph.Datasource + "-" + graph.Name + "</option>");
        }
        pageTemplate = pageTemplate.replace("{GRAPH_OPTIONS}", sbGraphsList.toString());
        return pageTemplate;
    }

    private void checkPost(HttpServletRequest rqst) {
        if ("POST".equals(rqst.getMethod())) {            
            if (rqst.getParameter("cbServices") != null) {
                this.cSelectedService = rqst.getParameter("cbServices");
            }

            if (rqst.getParameter("cbGraphs") != null) {
                this.cSelectedGraph = rqst.getParameter("cbGraphs");
            }
        }
    }

    private String loadTemplate() {
        String pageTemplate = templates.load("ServiceGraphPage.html");
        pageTemplate = pageTemplate.replace("{PAGE_TITLE}", nConfigHelper.getDisplayLanguage().getProperty("GRAPHPAGE_TITLE"));
        pageTemplate = pageTemplate.replace("{PAGE_GRAPH_SELECTOR}", nConfigHelper.getDisplayLanguage().getProperty("GRAPHPAGE_SELECTOR"));
        pageTemplate = pageTemplate.replace("{REFRESH}", nConfigHelper.getDisplayLanguage().getProperty("REFRESH"));
        return pageTemplate;
    }

    private String buildServiceList(String selectedService, String pageTemplate) {
        StringBuilder sbServiceList = new StringBuilder();
        for (nService service : nServiceHelper.AllServices()) {
            String selected = "";

            if (service.Name.equalsIgnoreCase(selectedService)) {
                selected = "selected";
                this.SelectedService = service;
            }

            sbServiceList.append("<option value=\"" + service.Name + "\"" + selected + " >" + service.Description + "</option>");
        }
        pageTemplate = pageTemplate.replace("{SERVICE_OPTIONS}", sbServiceList.toString());
        return pageTemplate;
    }

}
