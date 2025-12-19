package com.communityhub.servlet.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@WebListener
public class SessionListener implements HttpSessionListener {
    
    private static final Logger logger = Logger.getLogger(SessionListener.class.getName());
    private static final AtomicInteger activeSessions = new AtomicInteger(0);
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int currentSessions = activeSessions.incrementAndGet();
        
        // Set session timeout to 30 minutes
        se.getSession().setMaxInactiveInterval(30 * 60);
        
        logger.info("Session created. Session ID: " + se.getSession().getId() + 
                   ", Active sessions: " + currentSessions);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int currentSessions = activeSessions.decrementAndGet();
        
        // Log user logout if user was logged in
        Object user = se.getSession().getAttribute("user");
        String username = (String) se.getSession().getAttribute("username");
        
        if (user != null && username != null) {
            logger.info("User session destroyed: " + username + 
                       ", Active sessions: " + currentSessions);
        } else {
            logger.info("Anonymous session destroyed. Session ID: " + se.getSession().getId() + 
                       ", Active sessions: " + currentSessions);
        }
    }
    
    /**
     * Gets the current number of active sessions
     * @return Number of active sessions
     */
    public static int getActiveSessionCount() {
        return activeSessions.get();
    }
}