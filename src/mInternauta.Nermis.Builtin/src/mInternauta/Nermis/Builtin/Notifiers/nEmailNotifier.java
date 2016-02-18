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
package mInternauta.Nermis.Builtin.Notifiers;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import mInternauta.Nermis.Configs.nConfigHelper;
import mInternauta.Nermis.Core.nService;
import mInternauta.Nermis.Notifiers.nAbstractNotifier;
import static mInternauta.Nermis.Utils.nApplication.CurrentLogger;
import mInternauta.Nermis.Utils.nTemplateManager;

/**
 * Email Notifier
 */
public class nEmailNotifier extends nAbstractNotifier {
    private final nTemplateManager templates = new nTemplateManager();

    @Override
    public void NotifyServiceOffine(nService service, String message) {
        String serviceError = nConfigHelper.getDisplayLanguage().getProperty("SERVICE_OFFLINE");
        serviceError += service.Description + "<br />" + message;
        
        sendEmail(serviceError);
    }

    @Override
    public void NotifyServerError(String message) {
        String serverError = nConfigHelper.getDisplayLanguage().getProperty("SERVER_ERROR");
        serverError += " " + message;
        
        sendEmail(serverError);
    }

    private void sendEmail(String body) 
    {
        CurrentLogger.log(Level.INFO, "Sending notification email..");
        try {
            String emailTitle = nConfigHelper.getDisplayLanguage().getProperty("EMAIL_TITLE");
            String emailBody = nConfigHelper.getDisplayLanguage().getProperty("EMAIL_BODY");
            String hostname = InetAddress.getLocalHost().getHostName();
                        
            // Parse the Body
            emailBody = emailBody.replace("{NERMIS_HOSTNAME}", hostname);
            emailBody = emailBody.replace("{EMAIL_BODY}", body);
            
            // Load the template
            String tmpEmailBody = templates.load("Email.html");
            
            emailBody = tmpEmailBody.replace("{EMAIL}", emailBody);
            emailBody = emailBody.replace("{EMAIL_TITLE}", emailTitle);
            
            // Fetch server properties
            String smtpServer = this.getProperty("SmtpServer");
            String smtpPort = this.getProperty("SmtpPort");
            String smtpFrom = this.getProperty("SmtpFrom");
            String smtpPassword = this.getProperty("SmtpPassword");
            String smtpTo = this.getProperty("SmtpTo");
            String smtpFakeFrom = this.getProperty("SmtpFakeFrom");
            String smtpUseSsl = this.getProperty("SmtpUseSSL").trim();
            String smtpRequireAuth = this.getProperty("SmtpRequireAuth").trim();
            
            if(smtpFakeFrom == null) {
                smtpFakeFrom = smtpFrom;
            }
            
            // -
            Properties props = fetchProperties(smtpServer, smtpUseSsl, smtpPort, smtpRequireAuth);            
            Session session = createSession(smtpRequireAuth, props, smtpFrom, smtpPassword);            
            
            // - Create the Email            
            try {
                MimeMessage message = new MimeMessage(session);
                
                message.setFrom(new InternetAddress(smtpFakeFrom, "Nermis"));
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(smtpTo));
                message.setSubject(emailTitle);
                message.setContent(emailBody, "text/html; charset=utf-8");
                
                Transport.send(message);
            } catch (UnsupportedEncodingException | MessagingException ex) {
                CurrentLogger.log(Level.SEVERE, null, ex);
            }
        } catch (UnknownHostException ex) {
            CurrentLogger.log(Level.SEVERE, null, ex);
        }
    }

    private Session createSession(String smtpRequireAuth, Properties props, String smtpFrom, String smtpPassword) {
        Session session;
        
        if(smtpRequireAuth.equals("1")) {
            session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(smtpFrom, smtpPassword);
                }
            });
        }
        else
        {
            session = Session.getDefaultInstance(props);
        }
        
        return session;
    }

    private Properties fetchProperties(String smtpServer, String smtpUseSsl, String smtpPort, String smtpRequireAuth) {
        // Prepare the session
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", smtpServer.trim());
        if(smtpUseSsl.equals("1")) {
            props.put("mail.smtp.socketFactory.port", smtpPort.trim());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        if(smtpRequireAuth.equals("1")) {
            props.put("mail.smtp.auth", "true");
        }
        props.put("mail.smtp.port", smtpPort);
        return props;
    }
    
    @Override
    public String[] getProperties() {
        return new String[] 
        {
            "SmtpServer",
            "SmtpPort",
            "SmtpFrom",
            "SmtpFakeFrom",
            "SmtpTo",
            "SmtpPassword",
            "SmtpUseSSL",
            "SmtpRequireAuth"
        };
    }

    @Override
    public String getName() {
        return "Email";
    }
    
}
