<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ResoMap - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=2.0">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <div class="auth-logo">🗺️</div>
                <h1 class="auth-title">ResoMap</h1>
                <p class="auth-subtitle">Community Resource Hub</p>
            </div>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <span>${error}</span>
                </div>
            </c:if>
            
            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <span>${success}</span>
                </div>
            </c:if>
            
            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post" class="auth-form">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" placeholder="Enter your username" required>
                    <span class="error-text" id="usernameError"></span>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Enter your password" required>
                    <span class="error-text" id="passwordError"></span>
                </div>
                
                <button type="submit" class="btn btn-primary btn-block">Sign In</button>
            </form>
            
            <div class="auth-footer">
                <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one</a></p>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/validation.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            initializeLoginValidation();
        });
    </script>
</body>
</html>