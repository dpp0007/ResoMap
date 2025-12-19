<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ResoMap - Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=2.0">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <div class="auth-logo">🗺️</div>
                <h1 class="auth-title">Create Account</h1>
                <p class="auth-subtitle">Join the ResoMap community</p>
            </div>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <span>${error}</span>
                </div>
            </c:if>
            
            <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post" class="auth-form">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" value="${param.username}" placeholder="Choose a username" required>
                    <span class="error-text" id="usernameError"></span>
                </div>
                
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" value="${param.email}" placeholder="your@email.com" required>
                    <span class="error-text" id="emailError"></span>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Create a strong password" required>
                    <span class="error-text" id="passwordError"></span>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm your password" required>
                    <span class="error-text" id="confirmPasswordError"></span>
                </div>
                
                <div class="form-group">
                    <label for="role">I am a...</label>
                    <select id="role" name="role" required>
                        <option value="">Select your role</option>
                        <option value="VOLUNTEER" ${param.role == 'VOLUNTEER' ? 'selected' : ''}>Volunteer</option>
                        <option value="REQUESTER" ${param.role == 'REQUESTER' ? 'selected' : ''}>Requester</option>
                    </select>
                    <span class="error-text" id="roleError"></span>
                </div>
                
                <button type="submit" class="btn btn-primary btn-block">Create Account</button>
            </form>
            
            <div class="auth-footer">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a></p>
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/validation.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            initializeRegisterValidation();
        });
    </script>
</body>
</html>