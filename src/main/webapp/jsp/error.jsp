<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ResoMap - Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=2.0">
</head>
<body>
    <div class="error-container">
        <div class="error-card">
            <c:choose>
                <c:when test="${pageContext.errorData.statusCode == 404}">
                    <div class="error-code">404</div>
                    <h1 class="error-title">Page Not Found</h1>
                    <p class="error-message">The page you're looking for doesn't exist or has been moved.</p>
                </c:when>
                <c:when test="${pageContext.errorData.statusCode == 500}">
                    <div class="error-code">500</div>
                    <h1 class="error-title">Server Error</h1>
                    <p class="error-message">We're experiencing technical difficulties. Please try again later.</p>
                </c:when>
                <c:when test="${pageContext.errorData.statusCode == 403}">
                    <div class="error-code">403</div>
                    <h1 class="error-title">Access Denied</h1>
                    <p class="error-message">You don't have permission to access this resource.</p>
                </c:when>
                <c:otherwise>
                    <div class="error-code">${pageContext.errorData.statusCode}</div>
                    <h1 class="error-title">Error</h1>
                    <p class="error-message">An unexpected error occurred.</p>
                </c:otherwise>
            </c:choose>
            
            <c:if test="${not empty errorMessage}">
                <div class="error-details">
                    <strong>Details:</strong> ${errorMessage}
                </div>
            </c:if>
            
            <div class="error-actions">
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Go to Dashboard</a>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Login</a>
                <button onclick="history.back()" class="btn btn-outline">Go Back</button>
            </div>
            
            <c:if test="${pageContext.errorData.statusCode == 500 && not empty pageContext.exception}">
                <details class="error-details" style="margin-top: var(--space-lg);">
                    <summary style="cursor: pointer; color: var(--gray-500); font-size: 12px;">Technical Details</summary>
                    <pre style="margin-top: var(--space-sm); font-size: 11px; overflow-x: auto;">${pageContext.exception.message}</pre>
                </details>
            </c:if>
        </div>
    </div>
</body>
</html>