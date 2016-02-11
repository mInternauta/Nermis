# Nermis
----------
A small project that monitors online services and displays a web page that says if they are online.

## Screenshot
![](http://i.imgur.com/3M6kGPP.png)

## How do i run?

    For Windows:
    
   	To start in CLI (Console Mode):
    start.bat 
    
    To Start in Service Mode:
    start-service.bat
    
    For Linux:
    
    To start in CLI (Console Mode):
    sh start.sh 
    
    To Start in Service Mode:
    sh start-service.sh
    

Just type "help" for a list of commands in CLI mode, you will need to configure and create services for the Nermis (Type "services -help" for services help)

The default web page is:

http://localhost:5000/

http://localhost:5000/status.do

## Installing Service in Linux 

First step:

Change the installation directory in the SCRIPT variable on the nermis-deamon.sh file!

Second step:

Copy and install the deamon script

	cp "nermis-deamon.sh" "/etc/init.d/NermisService"
	chmod +x /etc/init.d/NermisService
	update-rc.d NermisService defaults

## How do i Download?
Check for Releases in Github page.

## Thanks and Licenses
Thanks for Apache Commons with Commons IO, NET and CLI Library

Thanks for MySQL Team with MySQL Connector for Java

Thanks for Quartz Scheduler Team with Quarts Scheduler 

Thanks for Jetty Team with Jetty WebServer
