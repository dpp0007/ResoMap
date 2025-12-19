package com.communityhub.servlet.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/dashboard/*", "/resources/*", "/requests/*"})
public class AuthFilter implements Filter {
    
    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Get session
        HttpSession session = httpRequest.getSession(false);
        
        // Check if user is logged in
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        if (isLoggedIn) {
            // User is authenticated, continue with request
            chain.doFilter(request, response);
        } else {
            // User is not authenticated, redirect to login
            String loginURL = httpRequest.getContextPath() + "/login";
            
            // Store the original requested URL for redirect after login
            String requestedURL = httpRequest.getRequestURL().toString();
            String queryString = httpRequest.getQueryString();
            
            if (queryString != null) {
                requestedURL += "?" + queryString;
            }
            
            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute("redirectURL", requestedURL);
            
            logger.info("Unauthorized access attempt to: " + requestedURL + 
                       " - redirecting to login");
            
            httpResponse.sendRedirect(loginURL);
        }
    }
    
    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }
}