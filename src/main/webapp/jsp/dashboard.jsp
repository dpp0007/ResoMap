<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ResoMap - Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=2.0">
</head>
<body>
    
    <!-- Sidebar Navigation -->
    <aside class="sidebar">
        <div class="sidebar-brand">
            <span class="sidebar-brand-icon">🗺️</span>
            <span class="sidebar-brand-text">ResoMap</span>
        </div>
        
        <ul class="sidebar-menu">
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-menu-link active" title="Dashboard">
                    <span class="sidebar-menu-icon">📊</span>
                    <span class="sidebar-menu-label">Dashboard</span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/resources" class="sidebar-menu-link" title="Resources">
                    <span class="sidebar-menu-icon">📦</span>
                    <span class="sidebar-menu-label">Resources</span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/requests" class="sidebar-menu-link" title="Requests">
                    <span class="sidebar-menu-icon">📋</span>
                    <span class="sidebar-menu-label">Requests</span>
                </a>
            </li>
            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                <li class="sidebar-menu-item">
                    <a href="${pageContext.request.contextPath}/admin" class="sidebar-menu-link" title="Admin Panel">
                        <span class="sidebar-menu-icon">⚙️</span>
                        <span class="sidebar-menu-label">Admin Panel</span>
                    </a>
                </li>
            </c:if>
        </ul>
        
        <div class="sidebar-footer">
            <div class="sidebar-footer-text">Current Role</div>
            <div class="sidebar-footer-content">
                <span class="sidebar-footer-role">${sessionScope.user.role}</span>
                <a href="${pageContext.request.contextPath}/logout" class="sidebar-logout-btn">🚪 Logout</a>
            </div>
        </div>
    </aside>
    
    <!-- Main Content -->
    <div class="main-layout">
        <main class="main-content">
            <div class="page-header">
                <h1 class="page-title">Dashboard</h1>
            </div>
            
            <!-- KEY METRICS - PRIMARY FOCUS -->
            <div class="grid grid-3 mb-lg">
                <div class="stat-card primary-metric">
                    <div class="stat-card-label">📊 Active Requests</div>
                    <div class="stat-card-value">${stats.activeRequests}</div>
                    <div class="stat-card-change">requests in progress</div>
                </div>
                <div class="stat-card primary-metric success-variant">
                    <div class="stat-card-label">✅ Completed</div>
                    <div class="stat-card-value">${stats.completedRequests}</div>
                    <div class="stat-card-change">requests finished</div>
                </div>
                <div class="stat-card primary-metric warning-variant">
                    <div class="stat-card-label">📦 Total Resources</div>
                    <div class="stat-card-value">${stats.totalResources}</div>
                    <div class="stat-card-change">available items</div>
                </div>
            </div>
            
            <!-- SECONDARY METRICS -->
            <div class="section mb-lg">
                <div class="section-header">
                    <h2 class="section-title">System Overview</h2>
                </div>
                <div class="grid grid-2">
                    <div class="stat-card secondary-metric">
                        <div class="stat-card-label">👥 Total Volunteers</div>
                        <div class="stat-card-value">${stats.totalVolunteers}</div>
                        <div class="stat-card-change">active volunteers</div>
                    </div>
                    <div class="stat-card secondary-metric">
                        <div class="stat-card-label">📈 System Health</div>
                        <div class="stat-card-value">100%</div>
                        <div class="stat-card-change">all systems operational</div>
                    </div>
                </div>
            </div>
            
            <!-- VOLUNTEER-SPECIFIC METRICS -->
            <c:if test="${sessionScope.user.role == 'VOLUNTEER'}">
                <div class="section mb-lg">
                    <div class="section-header">
                        <h2 class="section-title">Your Performance</h2>
                    </div>
                    <div class="grid grid-3">
                        <div class="stat-card primary-metric">
                            <div class="stat-card-label">⚡ Active Assignments</div>
                            <div class="stat-card-value">${stats.volunteerActive}</div>
                            <div class="stat-card-change">requests in progress</div>
                        </div>
                        <div class="stat-card primary-metric success-variant">
                            <div class="stat-card-label">✅ Completed</div>
                            <div class="stat-card-value">${stats.volunteerCompleted}</div>
                            <div class="stat-card-change">requests finished</div>
                        </div>
                        <div class="stat-card secondary-metric">
                            <div class="stat-card-label">⏱️ Avg Time</div>
                            <div class="stat-card-value">${stats.volunteerAvgTime}h</div>
                            <div class="stat-card-change">hours per request</div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- REQUESTER-SPECIFIC METRICS -->
            <c:if test="${sessionScope.user.role == 'REQUESTER'}">
                <div class="section mb-lg">
                    <div class="section-header">
                        <h2 class="section-title">Your Requests</h2>
                    </div>
                    <div class="grid grid-2">
                        <div class="stat-card primary-metric">
                            <div class="stat-card-label">📋 Active Requests</div>
                            <div class="stat-card-value">${stats.requesterActive}</div>
                            <div class="stat-card-change">pending or in progress</div>
                        </div>
                        <div class="stat-card primary-metric success-variant">
                            <div class="stat-card-label">✅ Completed</div>
                            <div class="stat-card-value">${stats.requesterCompleted}</div>
                            <div class="stat-card-change">finished requests</div>
                        </div>
                    </div>
                    <div class="grid grid-2 mt-lg">
                        <div class="stat-card secondary-metric">
                            <div class="stat-card-label">📊 Total Requests</div>
                            <div class="stat-card-value">${stats.requesterTotal}</div>
                            <div class="stat-card-change">all time</div>
                        </div>
                        <div class="stat-card secondary-metric danger-variant">
                            <div class="stat-card-label">❌ Cancelled</div>
                            <div class="stat-card-value">${stats.requesterCancelled}</div>
                            <div class="stat-card-change">cancelled requests</div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Quick Actions -->
            <div class="section mb-lg">
                <div class="section-header">
                    <h2 class="section-title">Quick Actions</h2>
                </div>
                <div class="quick-actions-grid">
                    <c:if test="${sessionScope.user.role == 'REQUESTER'}">
                        <a href="${pageContext.request.contextPath}/requests?action=create" class="action-card action-primary">
                            <span class="action-icon">➕</span>
                            <span class="action-text">Create Request</span>
                        </a>
                    </c:if>
                    <c:if test="${sessionScope.user.role == 'VOLUNTEER'}">
                        <a href="${pageContext.request.contextPath}/requests" class="action-card action-primary">
                            <span class="action-icon">👀</span>
                            <span class="action-text">View Requests</span>
                        </a>
                    </c:if>
                    <c:if test="${sessionScope.user.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/resources?action=create" class="action-card action-primary">
                            <span class="action-icon">➕</span>
                            <span class="action-text">Add Resource</span>
                        </a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/resources" class="action-card action-secondary">
                        <span class="action-icon">📦</span>
                        <span class="action-text">Browse Resources</span>
                    </a>
                </div>
            </div>
            
            <!-- Recent Activity -->
            <div class="section">
                <div class="section-header">
                    <h2 class="section-title">Recent Activity</h2>
                </div>
                <c:choose>
                    <c:when test="${empty recentActivity}">
                        <div class="empty-state">
                            <div class="empty-state-icon">📭</div>
                            <div class="empty-state-title">No Activity Yet</div>
                            <div class="empty-state-text">Your recent activity will appear here</div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <ul class="activity-timeline">
                            <c:forEach var="activity" items="${recentActivity}">
                                <li class="activity-item">
                                    <div class="activity-dot"></div>
                                    <div class="activity-content">
                                        <div class="activity-time">${activity.timestamp}</div>
                                        <div class="activity-description">${activity.description}</div>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/navbar.js"></script>
</body>
</html>