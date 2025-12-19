<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Requests - Community Resource Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=2.0">
    <style>
        .requests-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .requests-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        
        .requests-header h1 {
            margin: 0;
            color: #333;
        }
        
        .btn-new-request {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-new-request:hover {
            background-color: #45a049;
        }
        
        .filter-section {
            background-color: #f5f5f5;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .filter-group {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
        }
        
        .filter-group select {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .status-select, .urgency-select {
            padding: 6px 8px;
            border: 1px solid #2196F3;
            border-radius: 4px;
            font-size: 13px;
            background-color: #f0f8ff;
            cursor: pointer;
            min-width: 120px;
        }
        
        .status-select:hover, .urgency-select:hover {
            background-color: #e0f0ff;
        }
        
        .requests-table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .requests-table thead {
            background-color: #2196F3;
            color: white;
        }
        
        .requests-table th {
            padding: 12px;
            text-align: left;
            font-weight: 600;
        }
        
        .requests-table td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
        }
        
        .requests-table tbody tr:hover {
            background-color: #f5f5f5;
        }
        
        .status-badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .status-pending {
            background-color: #FFC107;
            color: #333;
        }
        
        .status-assigned {
            background-color: #2196F3;
            color: white;
        }
        
        .status-in-progress {
            background-color: #FF9800;
            color: white;
        }
        
        .status-completed {
            background-color: #4CAF50;
            color: white;
        }
        
        .status-cancelled {
            background-color: #f44336;
            color: white;
        }
        
        .urgency-badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .urgency-low {
            background-color: #4CAF50;
            color: white;
        }
        
        .urgency-medium {
            background-color: #FFC107;
            color: #333;
        }
        
        .urgency-high {
            background-color: #FF9800;
            color: white;
        }
        
        .urgency-critical {
            background-color: #f44336;
            color: white;
        }
        
        .actions {
            display: flex;
            gap: 8px;
        }
        
        .btn-small {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-view {
            background-color: #2196F3;
            color: white;
        }
        
        .btn-view:hover {
            background-color: #0b7dda;
        }
        
        .btn-update {
            background-color: #4CAF50;
            color: white;
        }
        
        .btn-update:hover {
            background-color: #45a049;
        }
        
        .btn-assign {
            background-color: #FF9800;
            color: white;
        }
        
        .btn-assign:hover {
            background-color: #e68900;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        
        .empty-state p {
            font-size: 16px;
            margin-bottom: 20px;
        }
        
        .alert {
            padding: 12px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.4);
        }
        
        .modal-content {
            background-color: #fefefe;
            margin: 10% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 80%;
            max-width: 500px;
            border-radius: 4px;
        }
        
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .close {
            color: #aaa;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }
        
        .close:hover {
            color: black;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #333;
        }
        
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            font-family: Arial, sans-serif;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        
        .btn-submit {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-submit:hover {
            background-color: #45a049;
        }
        
        .btn-cancel {
            background-color: #999;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin-left: 10px;
        }
        
        .btn-cancel:hover {
            background-color: #888;
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
                <a href="${pageContext.request.contextPath}/requests" class="sidebar-menu-link active" title="Requests">
                    <span class="sidebar-menu-icon">📋</span>
                    <span class="sidebar-menu-label">Requests</span>
                </a>
            </li>
            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                <li class="sidebar-menu-item">
                    <a href="${pageContext.request.contextPath}/admin" class="sidebar-menu-link" title="Admin Panel">
                        <span class="sidebar-menu-icon">⚙️</span>
                        <span class="sidebar-menu-label">Admin</span>
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
    
    <div class="main-layout">
        <main class="main-content">
            <div class="requests-page-header">
                <div class="requests-header-content">
                    <h1 class="page-title">Requests</h1>
                    <p class="page-subtitle">Track and manage resource requests</p>
                </div>
                <c:if test="${userRole != 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/requests?action=new" class="btn btn-primary btn-with-icon">
                        <span class="btn-icon">➕</span>
                        <span class="btn-label">New Request</span>
                    </a>
                </c:if>
            </div>
            
            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <span>${success}</span>
                </div>
                <c:set var="success" value="" scope="session"/>
            </c:if>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <span>${error}</span>
                </div>
            </c:if>
            
            <!-- Filter Toolbar -->
            <div class="requests-filter-toolbar">
                <div class="filter-group">
                    <label class="filter-label">Filter by:</label>
                    <select id="statusFilter" class="filter-select" onchange="filterRequests()">
                        <option value="">All Statuses</option>
                        <option value="PENDING">Pending</option>
                        <option value="ASSIGNED">Assigned</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="CANCELLED">Cancelled</option>
                    </select>
                    
                    <select id="urgencyFilter" class="filter-select" onchange="filterRequests()">
                        <option value="">All Urgency Levels</option>
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                        <option value="CRITICAL">Critical</option>
                    </select>
                    
                    <button class="filter-clear-btn" onclick="clearFilters()">Clear Filters</button>
                </div>
            </div>
        
            <c:choose>
                <c:when test="${empty requests}">
                    <div class="empty-state-container">
                        <div class="empty-state">
                            <div class="empty-state-icon">📋</div>
                            <div class="empty-state-title">No Requests Found</div>
                            <div class="empty-state-text">
                                <c:if test="${userRole == 'ADMIN'}">
                                    No requests have been created yet. Requests will appear here once users start creating them.
                                </c:if>
                                <c:if test="${userRole != 'ADMIN'}">
                                    You haven't created any requests yet. Start by creating your first request.
                                </c:if>
                            </div>
                            <c:if test="${userRole != 'ADMIN'}">
                                <a href="${pageContext.request.contextPath}/requests?action=new" class="btn btn-primary mt-lg">Create First Request</a>
                            </c:if>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="requests-table-wrapper">
                        <table class="requests-table">
                            <thead>
                                <tr>
                                    <th class="col-id">Request ID</th>
                                    <th class="col-resource">Resource</th>
                                    <th class="col-description">Description</th>
                                    <th class="col-status">Status</th>
                                    <th class="col-urgency">Urgency</th>
                                    <th class="col-created">Created</th>
                                    <th class="col-actions">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="request" items="${requests}">
                                    <tr class="request-row" data-request-id="${request.requestId}">
                                        <td class="col-id">
                                            <span class="request-id-badge">${fn:substring(request.requestId, 0, 8)}...</span>
                                        </td>
                                        <td class="col-resource">
                                            <span class="resource-name">${request.resourceId}</span>
                                        </td>
                                        <td class="col-description">
                                            <span class="description-text">${request.description}</span>
                                        </td>
                                        <td class="col-status">
                                            <c:if test="${userRole == 'ADMIN'}">
                                                <select id="status-${request.requestId}" class="status-select" onchange="updateRequestStatus('${request.requestId}', this.value)">
                                                    <option value="PENDING" <c:if test="${request.status == 'PENDING'}">selected</c:if>>PENDING</option>
                                                    <option value="ASSIGNED" <c:if test="${request.status == 'ASSIGNED'}">selected</c:if>>ASSIGNED</option>
                                                    <option value="IN_PROGRESS" <c:if test="${request.status == 'IN_PROGRESS'}">selected</c:if>>IN_PROGRESS</option>
                                                    <option value="COMPLETED" <c:if test="${request.status == 'COMPLETED'}">selected</c:if>>COMPLETED</option>
                                                    <option value="CANCELLED" <c:if test="${request.status == 'CANCELLED'}">selected</c:if>>CANCELLED</option>
                                                </select>
                                            </c:if>
                                            <c:if test="${userRole != 'ADMIN'}">
                                                <span class="status-badge status-${fn:toLowerCase(request.status)}">
                                                    ${request.status}
                                                </span>
                                            </c:if>
                                        </td>
                                        <td class="col-urgency">
                                            <c:if test="${userRole == 'ADMIN'}">
                                                <select id="urgency-${request.requestId}" class="urgency-select" onchange="updateRequestUrgency('${request.requestId}', this.value)">
                                                    <option value="LOW" <c:if test="${request.urgencyLevel == 'LOW'}">selected</c:if>>LOW</option>
                                                    <option value="MEDIUM" <c:if test="${request.urgencyLevel == 'MEDIUM'}">selected</c:if>>MEDIUM</option>
                                                    <option value="HIGH" <c:if test="${request.urgencyLevel == 'HIGH'}">selected</c:if>>HIGH</option>
                                                    <option value="CRITICAL" <c:if test="${request.urgencyLevel == 'CRITICAL'}">selected</c:if>>CRITICAL</option>
                                                </select>
                                            </c:if>
                                            <c:if test="${userRole != 'ADMIN'}">
                                                <span class="urgency-badge urgency-${fn:toLowerCase(request.urgencyLevel)}">
                                                    ${request.urgencyLevel}
                                                </span>
                                            </c:if>
                                        </td>
                                        <td class="col-created">
                                            <span class="created-date">${request.createdAt}</span>
                                        </td>
                                        <td class="col-actions">
                                            <div class="action-menu-container">
                                                <button class="action-menu-btn" onclick="toggleActionMenu(event, '${request.requestId}')">⋮</button>
                                                <div class="action-menu" id="menu-${request.requestId}">
                                                    <!-- ADMIN ACTIONS -->
                                                    <c:if test="${userRole == 'ADMIN'}">
                                                        <button class="action-menu-item" onclick="openAssignModal('${request.requestId}', '${request.volunteerId}')">
                                                            <span class="action-icon">👤</span>
                                                            <c:if test="${empty request.volunteerId}">Assign Volunteer</c:if>
                                                            <c:if test="${not empty request.volunteerId}">Reassign Volunteer</c:if>
                                                        </button>
                                                        <c:if test="${not empty request.volunteerId}">
                                                            <button class="action-menu-item action-danger" onclick="unassignVolunteer('${request.requestId}')">
                                                                <span class="action-icon">✕</span>
                                                                Unassign
                                                            </button>
                                                        </c:if>
                                                    </c:if>
                                                    
                                                    <!-- VOLUNTEER ACTIONS -->
                                                    <c:if test="${userRole == 'VOLUNTEER'}">
                                                        <c:if test="${request.status == 'ASSIGNED'}">
                                                            <button class="action-menu-item" onclick="volunteerAcceptRequest('${request.requestId}')">
                                                                <span class="action-icon">✓</span>
                                                                Accept Request
                                                            </button>
                                                            <button class="action-menu-item" onclick="volunteerStartWork('${request.requestId}')">
                                                                <span class="action-icon">▶</span>
                                                                Start Work
                                                            </button>
                                                            <button class="action-menu-item action-danger" onclick="openRejectModal('${request.requestId}')">
                                                                <span class="action-icon">✕</span>
                                                                Reject
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${request.status == 'IN_PROGRESS'}">
                                                            <button class="action-menu-item" onclick="openCompleteModal('${request.requestId}')">
                                                                <span class="action-icon">✓</span>
                                                                Mark Completed
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${request.status == 'ASSIGNED' || request.status == 'IN_PROGRESS'}">
                                                            <button class="action-menu-item" onclick="openNoteModal('${request.requestId}')">
                                                                <span class="action-icon">📝</span>
                                                                Add Note
                                                            </button>
                                                        </c:if>
                                                    </c:if>
                                                    
                                                    <!-- REQUESTER ACTIONS -->
                                                    <c:if test="${userRole == 'REQUESTER'}">
                                                        <c:if test="${request.status == 'PENDING'}">
                                                            <button class="action-menu-item" onclick="openEditModal('${request.requestId}', '${request.description}', '${request.urgencyLevel}')">
                                                                <span class="action-icon">✏️</span>
                                                                Edit Request
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${request.status == 'PENDING' || request.status == 'ASSIGNED'}">
                                                            <button class="action-menu-item action-danger" onclick="openCancelModal('${request.requestId}')">
                                                                <span class="action-icon">✕</span>
                                                                Cancel Request
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${request.status != 'COMPLETED' && request.status != 'CANCELLED'}">
                                                            <button class="action-menu-item" onclick="openRequesterNoteModal('${request.requestId}')">
                                                                <span class="action-icon">📝</span>
                                                                Add Note
                                                            </button>
                                                        </c:if>
                                                        <c:if test="${request.status == 'COMPLETED'}">
                                                            <button class="action-menu-item" onclick="openFeedbackModal('${request.requestId}')">
                                                                <span class="action-icon">⭐</span>
                                                                Give Feedback
                                                            </button>
                                                        </c:if>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
    
    <!-- Reject Request Modal (Volunteer Only) -->
    <div id="rejectModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Reject Request</h2>
                <span class="close" onclick="closeRejectModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/volunteer-action">
                <input type="hidden" name="action" value="reject-request">
                <input type="hidden" name="requestId" id="rejectRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label for="rejectReason">Reason for Rejection:</label>
                    <textarea id="rejectReason" name="reason" class="notes-textarea" placeholder="Please explain why you cannot complete this request..." required></textarea>
                </div>
                
                <button type="submit" class="btn-submit">Reject Request</button>
                <button type="button" class="btn-cancel" onclick="closeRejectModal()">Cancel</button>
            </form>
        </div>
    </div>
    
    <!-- Mark Completed Modal (Volunteer Only) -->
    <div id="completeModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Mark Request as Completed</h2>
                <span class="close" onclick="closeCompleteModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/volunteer-action">
                <input type="hidden" name="action" value="mark-completed">
                <input type="hidden" name="requestId" id="completeRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label for="completionNotes">Completion Notes (Optional):</label>
                    <textarea id="completionNotes" name="completionNotes" class="notes-textarea" placeholder="Describe what was completed..."></textarea>
                </div>
                
                <button type="submit" class="btn-submit">Mark as Completed</button>
                <button type="button" class="btn-cancel" onclick="closeCompleteModal()">Cancel</button>
            </form>
        </div>
    </div>
    
    <!-- Add Note Modal (Volunteer Only) -->
    <div id="noteModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Add Progress Note</h2>
                <span class="close" onclick="closeNoteModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/volunteer-action">
                <input type="hidden" name="action" value="add-note">
                <input type="hidden" name="requestId" id="noteRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label for="noteText">Progress Note:</label>
                    <textarea id="noteText" name="note" class="notes-textarea" placeholder="Add a progress update..." required></textarea>
                </div>
                
                <button type="submit" class="btn-submit">Add Note</button>
                <button type="button" class="btn-cancel" onclick="closeNoteModal()">Cancel</button>
            </form>
        </div>
    </div>
    
    <!-- Edit Request Modal (Requester Only) -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Edit Request</h2>
                <span class="close" onclick="closeEditModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/requester-action">
                <input type="hidden" name="action" value="edit-request">
                <input type="hidden" name="requestId" id="editRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label for="editDescription">Description:</label>
                    <textarea id="editDescription" name="description" class="notes-textarea" placeholder="Describe your request..." required></textarea>
                </div>
                
                <div class="form-group">
                    <label for="editUrgency">Urgency Level:</label>
                    <select id="editUrgency" name="urgencyLevel" required>
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                        <option value="CRITICAL">Critical</option>
                    </select>
                </div>
                
                <button type="submit" class="btn-submit">Save Changes</button>
                <button type="button" class="btn-cancel" onclick="closeEditModal()">Cancel</button>
            </form>
        </div>
    </div>
    
    <!-- Cancel Request Modal (Requester Only) -->
    <div id="cancelModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Cancel Request</h2>
                <span class="close" onclick="closeCancelModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/requester-action">
                <input type="hidden" name="action" value="cancel-request">
                <input type="hidden" name="requestId" id="cancelRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label for="cancelReason">Reason for Cancellation (Optional):</label>
                    <textarea id="cancelReason" name="reason" class="notes-textarea" placeholder="Why are you cancelling this request?"></textarea>
                </div>
                
                <button type="submit" class="btn-submit">Confirm Cancellation</button>
                <button type="button" class="btn-cancel" onclick="closeCancelModal()">Keep Request</button>
            </form>
        </div>
    </div>
    
    <!-- Add Note Modal (Requester Only) -->
    <div id="requesterNoteModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Add Note to Request</h2>
                <span class="close" onclick="closeRequesterNoteModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/requester-action">
                <input type="hidden" name="action" value="add-note">
                <input type="hidden" name="requestId" id="requesterNoteRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label for="requesterNoteText">Your Note:</label>
                    <textarea id="requesterNoteText" name="note" class="notes-textarea" placeholder="Add any additional information..." required></textarea>
                </div>
                
                <button type="submit" class="btn-submit">Add Note</button>
                <button type="button" class="btn-cancel" onclick="closeRequesterNoteModal()">Cancel</button>
            </form>
        </div>
    </div>
    
    <!-- Feedback Modal (Requester Only) -->
    <div id="feedbackModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Submit Feedback</h2>
                <span class="close" onclick="closeFeedbackModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/requester-action">
                <input type="hidden" name="action" value="submit-feedback">
                <input type="hidden" name="requestId" id="feedbackRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label>Rating (Required):</label>
                    <div class="rating-input">
                        <input type="radio" id="rating1" name="rating" value="1" required>
                        <label for="rating1">1 - Poor</label>
                        <input type="radio" id="rating2" name="rating" value="2">
                        <label for="rating2">2 - Fair</label>
                        <input type="radio" id="rating3" name="rating" value="3">
                        <label for="rating3">3 - Good</label>
                        <input type="radio" id="rating4" name="rating" value="4">
                        <label for="rating4">4 - Very Good</label>
                        <input type="radio" id="rating5" name="rating" value="5">
                        <label for="rating5">5 - Excellent</label>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="feedbackComment">Comment (Optional):</label>
                    <textarea id="feedbackComment" name="comment" class="feedback-textarea" placeholder="Share your feedback..."></textarea>
                </div>
                
                <button type="submit" class="btn-submit">Submit Feedback</button>
                <button type="button" class="btn-cancel" onclick="closeFeedbackModal()">Cancel</button>
            </form>
        </div>
    </div>
    
    <!-- Assign/Reassign Volunteer Modal (Admin Only) -->
    <div id="assignModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2 id="assignModalTitle">Assign Volunteer</h2>
                <span class="close" onclick="closeAssignModal()">&times;</span>
            </div>
            <form method="POST" action="${pageContext.request.contextPath}/request-management">
                <input type="hidden" name="action" id="assignAction" value="assign-volunteer">
                <input type="hidden" name="requestId" id="assignRequestId">
                <input type="hidden" name="redirectUrl" value="${pageContext.request.requestURI}">
                
                <div class="form-group">
                    <label for="volunteerSelect">Select Volunteer:</label>
                    <select id="volunteerSelect" name="volunteerId" required>
                        <option value="">-- Select a volunteer --</option>
                    </select>
                </div>
                
                <button type="submit" class="btn-submit">Confirm Assignment</button>
                <button type="button" class="btn-cancel" onclick="closeAssignModal()">Cancel</button>
            </form>
        </div>
    </div>
    
    <script>
        // Volunteer list (populated from server-side data)
        const volunteers = [
            <c:if test="${not empty volunteers}">${volunteers}</c:if>
        ];
        
        // ===== VOLUNTEER ACTION FUNCTIONS =====
        
        function volunteerAcceptRequest(requestId) {
            if (confirm('Accept this request? You will be responsible for completing it.')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/volunteer-action';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'accept-request';
                
                const requestIdInput = document.createElement('input');
                requestIdInput.type = 'hidden';
                requestIdInput.name = 'requestId';
                requestIdInput.value = requestId;
                
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.name = 'redirectUrl';
                redirectInput.value = window.location.href;
                
                form.appendChild(actionInput);
                form.appendChild(requestIdInput);
                form.appendChild(redirectInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        function openRejectModal(requestId) {
            document.getElementById('rejectRequestId').value = requestId;
            document.getElementById('rejectModal').style.display = 'block';
        }
        
        function closeRejectModal() {
            document.getElementById('rejectModal').style.display = 'none';
        }
        
        function volunteerStartWork(requestId) {
            if (confirm('Start work on this request?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/volunteer-action';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'start-work';
                
                const requestIdInput = document.createElement('input');
                requestIdInput.type = 'hidden';
                requestIdInput.name = 'requestId';
                requestIdInput.value = requestId;
                
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.name = 'redirectUrl';
                redirectInput.value = window.location.href;
                
                form.appendChild(actionInput);
                form.appendChild(requestIdInput);
                form.appendChild(redirectInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        function openCompleteModal(requestId) {
            document.getElementById('completeRequestId').value = requestId;
            document.getElementById('completeModal').style.display = 'block';
        }
        
        function closeCompleteModal() {
            document.getElementById('completeModal').style.display = 'none';
        }
        
        function openNoteModal(requestId) {
            document.getElementById('noteRequestId').value = requestId;
            document.getElementById('noteModal').style.display = 'block';
        }
        
        function closeNoteModal() {
            document.getElementById('noteModal').style.display = 'none';
        }
        
        // ===== ADMIN ACTION FUNCTIONS =====
        
        function openAssignModal(requestId, currentVolunteerId) {
            document.getElementById('assignRequestId').value = requestId;
            
            // Determine if this is assign or reassign
            if (currentVolunteerId && currentVolunteerId.trim() !== '') {
                document.getElementById('assignModalTitle').textContent = 'Reassign Volunteer';
                document.getElementById('assignAction').value = 'reassign-volunteer';
            } else {
                document.getElementById('assignModalTitle').textContent = 'Assign Volunteer';
                document.getElementById('assignAction').value = 'assign-volunteer';
            }
            
            document.getElementById('assignModal').style.display = 'block';
        }
        
        function closeAssignModal() {
            document.getElementById('assignModal').style.display = 'none';
        }
        
        function unassignVolunteer(requestId) {
            if (confirm('Are you sure you want to unassign the volunteer from this request?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/request-management';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'unassign-volunteer';
                
                const requestIdInput = document.createElement('input');
                requestIdInput.type = 'hidden';
                requestIdInput.name = 'requestId';
                requestIdInput.value = requestId;
                
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.name = 'redirectUrl';
                redirectInput.value = window.location.href;
                
                form.appendChild(actionInput);
                form.appendChild(requestIdInput);
                form.appendChild(redirectInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        function updateRequestStatus(requestId, newStatus) {
            if (confirm('Change request status to ' + newStatus + '?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/request-management';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'change-status';
                
                const requestIdInput = document.createElement('input');
                requestIdInput.type = 'hidden';
                requestIdInput.name = 'requestId';
                requestIdInput.value = requestId;
                
                const statusInput = document.createElement('input');
                statusInput.type = 'hidden';
                statusInput.name = 'status';
                statusInput.value = newStatus;
                
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.name = 'redirectUrl';
                redirectInput.value = window.location.href;
                
                form.appendChild(actionInput);
                form.appendChild(requestIdInput);
                form.appendChild(statusInput);
                form.appendChild(redirectInput);
                document.body.appendChild(form);
                form.submit();
            } else {
                // Revert the select to previous value
                location.reload();
            }
        }
        
        function updateRequestUrgency(requestId, newUrgency) {
            if (confirm('Change request urgency to ' + newUrgency + '?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/request-management';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'change-urgency';
                
                const requestIdInput = document.createElement('input');
                requestIdInput.type = 'hidden';
                requestIdInput.name = 'requestId';
                requestIdInput.value = requestId;
                
                const urgencyInput = document.createElement('input');
                urgencyInput.type = 'hidden';
                urgencyInput.name = 'urgency';
                urgencyInput.value = newUrgency;
                
                const redirectInput = document.createElement('input');
                redirectInput.type = 'hidden';
                redirectInput.name = 'redirectUrl';
                redirectInput.value = window.location.href;
                
                form.appendChild(actionInput);
                form.appendChild(requestIdInput);
                form.appendChild(urgencyInput);
                form.appendChild(redirectInput);
                document.body.appendChild(form);
                form.submit();
            } else {
                // Revert the select to previous value
                location.reload();
            }
        }
        
        // Action Menu Toggle
        function toggleActionMenu(event, requestId) {
            event.stopPropagation();
            const menu = document.getElementById('menu-' + requestId);
            
            // Close all other menus
            document.querySelectorAll('.action-menu.active').forEach(m => {
                if (m.id !== 'menu-' + requestId) {
                    m.classList.remove('active');
                }
            });
            
            // Toggle current menu
            menu.classList.toggle('active');
        }
        
        // Close menu when clicking outside
        document.addEventListener('click', function() {
            document.querySelectorAll('.action-menu.active').forEach(menu => {
                menu.classList.remove('active');
            });
        });
        
        // Clear Filters
        function clearFilters() {
            document.getElementById('statusFilter').value = '';
            document.getElementById('urgencyFilter').value = '';
            filterRequests();
        }
        
        function filterRequests() {
            const statusFilter = document.getElementById('statusFilter').value;
            const urgencyFilter = document.getElementById('urgencyFilter').value;
            
            const rows = document.querySelectorAll('.requests-table tbody tr');
            rows.forEach(row => {
                let show = true;
                
                if (statusFilter) {
                    const statusCell = row.cells[3];
                    const status = statusCell.querySelector('select') ? statusCell.querySelector('select').value : statusCell.textContent.trim();
                    show = show && status.includes(statusFilter);
                }
                
                if (urgencyFilter) {
                    const urgencyCell = row.cells[4];
                    const urgency = urgencyCell.querySelector('select') ? urgencyCell.querySelector('select').value : urgencyCell.textContent.trim();
                    show = show && urgency.includes(urgencyFilter);
                }
                
                row.style.display = show ? '' : 'none';
            });
        }
        
        // ===== REQUESTER ACTION FUNCTIONS =====
        
        function openEditModal(requestId, description, urgency) {
            document.getElementById('editRequestId').value = requestId;
            document.getElementById('editDescription').value = description;
            document.getElementById('editUrgency').value = urgency;
            document.getElementById('editModal').style.display = 'block';
        }
        
        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
        }
        
        function openCancelModal(requestId) {
            document.getElementById('cancelRequestId').value = requestId;
            document.getElementById('cancelModal').style.display = 'block';
        }
        
        function closeCancelModal() {
            document.getElementById('cancelModal').style.display = 'none';
        }
        
        function openRequesterNoteModal(requestId) {
            document.getElementById('requesterNoteRequestId').value = requestId;
            document.getElementById('requesterNoteText').value = '';
            document.getElementById('requesterNoteModal').style.display = 'block';
        }
        
        function closeRequesterNoteModal() {
            document.getElementById('requesterNoteModal').style.display = 'none';
        }
        
        function openFeedbackModal(requestId) {
            document.getElementById('feedbackRequestId').value = requestId;
            document.getElementById('feedbackComment').value = '';
            document.querySelectorAll('input[name="rating"]').forEach(r => r.checked = false);
            document.getElementById('feedbackModal').style.display = 'block';
        }
        
        function closeFeedbackModal() {
            document.getElementById('feedbackModal').style.display = 'none';
        }
        
        // Close modal when clicking outside
        window.onclick = function(event) {
            const assignModal = document.getElementById('assignModal');
            const rejectModal = document.getElementById('rejectModal');
            const completeModal = document.getElementById('completeModal');
            const noteModal = document.getElementById('noteModal');
            const editModal = document.getElementById('editModal');
            const cancelModal = document.getElementById('cancelModal');
            const requesterNoteModal = document.getElementById('requesterNoteModal');
            const feedbackModal = document.getElementById('feedbackModal');
            
            if (event.target == assignModal) {
                assignModal.style.display = 'none';
            }
            if (event.target == rejectModal) {
                rejectModal.style.display = 'none';
            }
            if (event.target == completeModal) {
                completeModal.style.display = 'none';
            }
            if (event.target == noteModal) {
                noteModal.style.display = 'none';
            }
            if (event.target == editModal) {
                editModal.style.display = 'none';
            }
            if (event.target == cancelModal) {
                cancelModal.style.display = 'none';
            }
            if (event.target == requesterNoteModal) {
                requesterNoteModal.style.display = 'none';
            }
            if (event.target == feedbackModal) {
                feedbackModal.style.display = 'none';
            }
        }
    </script>
</body>
</html>
