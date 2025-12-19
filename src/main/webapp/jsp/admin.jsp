<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Panel - Community Resource Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=2.0">
    <style>
        .admin-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .admin-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            border-bottom: 2px solid #2196F3;
            padding-bottom: 15px;
        }
        
        .admin-header h1 {
            margin: 0;
            color: #333;
        }
        
        .admin-nav {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }
        
        .admin-nav a {
            padding: 10px 20px;
            background-color: #f5f5f5;
            border: 1px solid #ddd;
            border-radius: 4px;
            text-decoration: none;
            color: #333;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .admin-nav a:hover {
            background-color: #2196F3;
            color: white;
            border-color: #2196F3;
        }
        
        .admin-nav a.active {
            background-color: #2196F3;
            color: white;
            border-color: #2196F3;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            border-left: 4px solid #2196F3;
        }
        
        .stat-card h3 {
            margin: 0 0 10px 0;
            color: #666;
            font-size: 14px;
            text-transform: uppercase;
        }
        
        .stat-card .stat-number {
            margin: 0;
            font-size: 32px;
            font-weight: bold;
            color: #2196F3;
        }
        
        .admin-section {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        
        .admin-section h2 {
            margin-top: 0;
            color: #333;
            border-bottom: 2px solid #f0f0f0;
            padding-bottom: 10px;
        }
        
        .admin-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        
        .admin-table thead {
            background-color: #f5f5f5;
        }
        
        .admin-table th {
            padding: 12px;
            text-align: left;
            font-weight: 600;
            color: #333;
            border-bottom: 2px solid #ddd;
        }
        
        .admin-table td {
            padding: 12px;
            border-bottom: 1px solid #eee;
        }
        
        .admin-table tbody tr:hover {
            background-color: #f9f9f9;
        }
        
        .badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .badge-admin {
            background-color: #f44336;
            color: white;
        }
        
        .badge-volunteer {
            background-color: #4CAF50;
            color: white;
        }
        
        .badge-requester {
            background-color: #2196F3;
            color: white;
        }
        
        .badge-pending {
            background-color: #FFC107;
            color: #333;
        }
        
        .badge-assigned {
            background-color: #2196F3;
            color: white;
        }
        
        .badge-in-progress {
            background-color: #FF9800;
            color: white;
        }
        
        .badge-completed {
            background-color: #4CAF50;
            color: white;
        }
        
        .badge-cancelled {
            background-color: #999;
            color: white;
        }
        
        .alert {
            padding: 12px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #999;
        }
    </style>
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
                <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-menu-link" title="Dashboard">
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
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/admin" class="sidebar-menu-link active" title="Admin Panel">
                    <span class="sidebar-menu-icon">⚙️</span>
                    <span class="sidebar-menu-label">Admin Panel</span>
                </a>
            </li>
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
            <div class="admin-container">
        <div class="admin-header">
            <h1>Admin Panel</h1>
        </div>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>
        
        <!-- Statistics Section -->
        <div class="stats-grid">
            <div class="stat-card">
                <h3>Total Users</h3>
                <p class="stat-number">${totalUsers}</p>
            </div>
            <div class="stat-card">
                <h3>Total Resources</h3>
                <p class="stat-number">${totalResources}</p>
            </div>
            <div class="stat-card">
                <h3>Total Requests</h3>
                <p class="stat-number">${totalRequests}</p>
            </div>
            <div class="stat-card">
                <h3>Active Requests</h3>
                <p class="stat-number">${activeRequests}</p>
            </div>
            <div class="stat-card">
                <h3>Completed Requests</h3>
                <p class="stat-number">${completedRequests}</p>
            </div>
        </div>
        
        <!-- Users Management Section -->
        <div class="admin-section">
            <h2>Users Management</h2>
            <c:choose>
                <c:when test="${empty users}">
                    <div class="empty-state">
                        <p>No users found</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Created</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td>${user.username}</td>
                                    <td>${user.email}</td>
                                    <td>
                                        <span class="badge badge-${user.role.toString().toLowerCase()}">
                                            ${user.role}
                                        </span>
                                    </td>
                                    <td>
                                        <c:if test="${user.active}">
                                            <span style="color: green; font-weight: bold;">Active</span>
                                        </c:if>
                                        <c:if test="${!user.active}">
                                            <span style="color: red; font-weight: bold;">Inactive</span>
                                        </c:if>
                                    </td>
                                    <td>
                                        ${user.createdAt}
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 5px; flex-wrap: wrap;">
                                            <c:if test="${user.active}">
                                                <form method="POST" action="${pageContext.request.contextPath}/admin-action" style="display: inline;">
                                                    <input type="hidden" name="action" value="deactivate-user">
                                                    <input type="hidden" name="targetId" value="${user.userId}">
                                                    <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                                                    <button type="submit" class="btn-small" style="background-color: #f44336; color: white; padding: 6px 10px; border: none; border-radius: 4px; cursor: pointer;" onclick="return confirm('Deactivate this user?')">Deactivate</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${!user.active}">
                                                <form method="POST" action="${pageContext.request.contextPath}/admin-action" style="display: inline;">
                                                    <input type="hidden" name="action" value="activate-user">
                                                    <input type="hidden" name="targetId" value="${user.userId}">
                                                    <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                                                    <button type="submit" class="btn-small" style="background-color: #4CAF50; color: white; padding: 6px 10px; border: none; border-radius: 4px; cursor: pointer;">Activate</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${user.failedLoginAttempts > 0}">
                                                <form method="POST" action="${pageContext.request.contextPath}/admin-action" style="display: inline;">
                                                    <input type="hidden" name="action" value="reset-lockout">
                                                    <input type="hidden" name="targetId" value="${user.userId}">
                                                    <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                                                    <button type="submit" class="btn-small" style="background-color: #FF9800; color: white; padding: 6px 10px; border: none; border-radius: 4px; cursor: pointer;">Unlock</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Resources Management Section -->
        <div class="admin-section">
            <h2>Resources Management</h2>
            <c:choose>
                <c:when test="${empty resources}">
                    <div class="empty-state">
                        <p>No resources found</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Category</th>
                                <th>Quantity</th>
                                <th>Location</th>
                                <th>Created</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="resource" items="${resources}">
                                <tr>
                                    <td>${resource.name}</td>
                                    <td>${resource.category}</td>
                                    <td>${resource.quantity}</td>
                                    <td>${resource.location}</td>
                                    <td>
                                        ${resource.createdAt}
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 5px;">
                                            <button class="btn-small" style="background-color: #2196F3; color: white; padding: 6px 10px; border: none; border-radius: 4px; cursor: pointer;" onclick="editResourceQuantity('${resource.resourceId}', ${resource.quantity})">Edit Qty</button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Requests Management Section -->
        <div class="admin-section">
            <h2>Requests Management</h2>
            <c:choose>
                <c:when test="${empty requests}">
                    <div class="empty-state">
                        <p>No requests found</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>Request ID</th>
                                <th>Resource</th>
                                <th>Status</th>
                                <th>Urgency</th>
                                <th>Created</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="request" items="${requests}">
                                <tr>
                                    <td>${request.requestId}</td>
                                    <td>${request.resourceId}</td>
                                    <td>
                                        <span class="badge badge-${request.status.toString().toLowerCase()}">
                                            ${request.status}
                                        </span>
                                    </td>
                                    <td>${request.urgencyLevel}</td>
                                    <td>
                                        ${request.createdAt}
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 5px; flex-wrap: wrap;">
                                            <c:if test="${request.status != 'COMPLETED' && request.status != 'CANCELLED'}">
                                                <form method="POST" action="${pageContext.request.contextPath}/admin-action" style="display: inline;">
                                                    <input type="hidden" name="action" value="escalate-request">
                                                    <input type="hidden" name="targetId" value="${request.requestId}">
                                                    <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                                                    <button type="submit" class="btn-small" style="background-color: #FF9800; color: white; padding: 6px 10px; border: none; border-radius: 4px; cursor: pointer;" onclick="return confirm('Escalate this request to CRITICAL?')">Escalate</button>
                                                </form>
                                                <form method="POST" action="${pageContext.request.contextPath}/admin-action" style="display: inline;">
                                                    <input type="hidden" name="action" value="reject-request">
                                                    <input type="hidden" name="targetId" value="${request.requestId}">
                                                    <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                                                    <button type="submit" class="btn-small" style="background-color: #f44336; color: white; padding: 6px 10px; border: none; border-radius: 4px; cursor: pointer;" onclick="return confirm('Reject this request?')">Reject</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${request.status != 'COMPLETED' && request.status != 'CANCELLED'}">
                                                <form method="POST" action="${pageContext.request.contextPath}/admin-action" style="display: inline;">
                                                    <input type="hidden" name="action" value="force-close-request">
                                                    <input type="hidden" name="targetId" value="${request.requestId}">
                                                    <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                                                    <button type="submit" class="btn-small" style="background-color: #4CAF50; color: white; padding: 6px 10px; border: none; border-radius: 4px; cursor: pointer;" onclick="return confirm('Force-close this request?')">Close</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
            </div>
        </main>
    </div>
    
    <script>
        function editResourceQuantity(resourceId, currentQuantity) {
            const newQuantity = prompt('Enter new quantity:', currentQuantity);
            if (newQuantity !== null && newQuantity !== '') {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin-action';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'update-quantity';
                
                const targetInput = document.createElement('input');
                targetInput.type = 'hidden';
                targetInput.name = 'targetId';
                targetInput.value = resourceId;
                
                const quantityInput = document.createElement('input');
                quantityInput.type = 'hidden';
                quantityInput.name = 'quantity';
                quantityInput.value = newQuantity;
                
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.name = 'redirectUrl';
                redirectInput.value = window.location.href;
                
                form.appendChild(actionInput);
                form.appendChild(targetInput);
                form.appendChild(quantityInput);
                form.appendChild(redirectInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
    </script>
</body>
</html>
