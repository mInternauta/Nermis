-- 
-- Copyright (C) 2015 mInternauta
-- 
-- This program is free software; you can redistribute it and/or
-- modify it under the terms of the GNU General Public License
-- as published by the Free Software Foundation; either version 2
-- of the License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License
-- along with this program; if not, write to the Free Software
-- Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-- 
-- 

-- Custom Configurations
CUSTOM_OPTIONS = { example = "localhost" };
MESSAGES = { 
"Remote Desktop Connection to {HOSTNAME} is online and operational!", 
"Remote Desktop Connection to {HOSTNAME} is offline",
"Please fill with the valid information"
};
-- #
Context = getWebContext();
ResponseWriter = Context.Response:getWriter();

function showFormPage() 
	-- Load the Template 
	local tmplFile = getFile(Context, "formTemplate.html");
	local file = io.open(tmplFile:getAbsolutePath(), "rb");
	local formTemplate = file:read("*a");
	file:close();
	
	-- Load custom options
	local customHosts = "";
	for kCustom, vCustom in pairs(CUSTOM_OPTIONS) do 
		customHosts = customHosts .. '<input type="radio" name="hostname" value="' .. kCustom ..'"> ' .. vCustom .. " <br>"
	end 
	
	-- Build the template 
	formTemplate = string.gsub(formTemplate, "{CUSTOM_HOSTS}", customHosts);
	
	-- Send the Response 
	ResponseWriter:println(formTemplate);
end 

function showRespPage(resp)
	-- Load the Template 
	local tmplFile = getFile(Context, "respTemplate.html");
	local file = io.open(tmplFile:getAbsolutePath(), "rb");
	local formTemplate = file:read("*a");
	file:close();

	-- Build the template 
	formTemplate = string.gsub(formTemplate, "{RESPONSE}", resp);
	
	-- Send the Response 
	ResponseWriter:println(formTemplate);
end 

function processPost() 
	local msgResponse = "";
	local hostnameOpt = Context.Request:getParameter("hostname");
	local hostname = "";

	if(hostnameOpt == "custom") then 
		hostname = Context.Request:getParameter("txtHostname");
	else 
		hostname = CUSTOM_OPTIONS[hostnameOpt];
	end 
	
	local port = Context.Request:getParameter("txtPort");
	port = tonumber(port);
	
	if(port == nil or string.len(hostname) <= 0) then
		msgResponse = MESSAGES[3];
	else
		local socketUtils = luajava.bindClass("mInternauta.Nermis.Net.SocketUtils");
		local isOnline = socketUtils:checkTcpPort(hostname, port);
		
		if(isOnline == true) then 
			msgResponse = MESSAGES[1];
		else 
			msgResponse = MESSAGES[2];
		end 
		
		msgResponse = string.gsub(msgResponse, "{HOSTNAME}", hostname);
	end 
	
	showRespPage(msgResponse);
end 

if(Context.Request:getMethod() == "GET") then
	showFormPage();
elseif(Context.Request:getMethod() == "POST") then
	processPost();
end 

Context.Response:setStatus(200);