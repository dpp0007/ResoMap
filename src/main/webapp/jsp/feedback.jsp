<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Feedback - Community Resource Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=2.0">
    <style>
        .feedback-container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .feedback-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        
        .feedback-header h1 {
            margin: 0;
            color: #333;
        }
        
        .btn-new-feedback {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-new-feedback:hover {
            background-color: #45a049;
        }
        
        .form-container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .form-container h2 {
            color: #333;
            margin-top: 0;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
            font-size: 14px;
        }
        
        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            font-family: Arial, sans-serif;
            box-sizing: border-box;
        }
        
        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #2196F3;
            box-shadow: 0 0 5px rgba(33, 150, 243, 0.3);
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 120px;
        }
        
        .rating-group {
            display: flex;
            gap: 10px;
            margin-top: 10px;
        }
        
        .rating-option {
            display: flex;
            align-items: center;
            gap: 5px;
        }
        
        .rating-option input[type="radio"] {
            width: auto;
            margin: 0;
        }
        
        .rating-option label {
            margin: 0;
            font-weight: normal;
        }
        
        .form-actions {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }
        
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        .btn-submit {
            background-color: #4CAF50;
            color: white;
        }
        
        .btn-submit:hover {
            background-color: #45a049;
        }
        
        .btn-cancel {
            background-color: #999;
            color: white;
        }
        
        .btn-cancel:hover {
            background-color: #888;
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
        
        .feedback-list {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .feedback-item {
            padding: 20px;
            border-bottom: 1px solid #eee;
        }
        
        .feedback-item:last-child {
            border-bottom: none;
        }
        
        .feedback-header-item {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 10px;
        }
        
        .feedback-user {
            font-weight: 600;
            color: #333;
        }
        
        .feedback-date {
            color: #999;
            font-size: 12px;
        }
        
        .feedback-rating {
            color: #FFC107;
            font-size: 18px;
            margin-bottom: 10px;
        }
        
        .feedback-type {
            display: inline-block;
            background-color: #e3f2fd;
            color: #1976d2;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 10px;
        }
        
        .feedback-comments {
            color: #555;
            line-height: 1.6;
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
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar">
        <div class="navbar-container">
            <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">
                <span class="navbar-brand-icon">🗺️</span>
                <span class="navbar-brand-text">ResoMap</span>
            </a>
            <div class="navbar-menu">
                <div class="navbar-user">
                    <div class="navbar-user-avatar" title="User Avatar"><c:out value="${fn:toUpperCase(fn:substring(sessionScope.user.username, 0, 1))}"/></div>
                    <div class="navbar-user-info">
                        <span class="navbar-user-name">${sessionScope.user.username}</span>
                        <span class="navbar-user-role">${sessionScope.user.role}</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/logout" class="navbar-logout-btn">Logout</a>
                </div>
            </div>
        </div>
    </nav>
    
    <nav class="dashboard-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
            <li><a href="${pageContext.request.contextPath}/resources">Resources</a></li>
            <li><a href="${pageContext.request.contextPath}/requests">Requests</a></li>
            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                <li><a href="${pageContext.request.contextPath}/admin">Admin Panel</a></li>
            </c:if>
        </ul>
    </nav>
    
    <div class="feedback-container">
        <div class="feedback-header">
            <h1>Feedback & Ratings</h1>
            <a href="${pageContext.request.contextPath}/feedback?action=new" class="btn-new-feedback">
                + Submit Feedback
            </a>
        </div>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                ${success}
            </div>
            <c:set var="success" value="" scope="session"/>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>
        
        <!-- Feedback Form -->
        <c:if test="${param.action == 'new'}">
            <div class="form-container">
                <h2>Submit Your Feedback</h2>
                
                <form method="POST" action="${pageContext.request.contextPath}/feedback" onsubmit="return validateFeedbackForm()">
                    <input type="hidden" name="action" value="submit">
                    
                    <div class="form-group">
                        <label for="rating">Rating: <span style="color: red;">*</span></label>
                        <div class="rating-group">
                            <div class="rating-option">
                                <input type="radio" id="rating1" name="rating" value="1" required>
                                <label for="rating1">⭐ Poor</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="rating2" name="rating" value="2">
                                <label for="rating2">⭐⭐ Fair</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="rating3" name="rating" value="3">
                                <label for="rating3">⭐⭐⭐ Good</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="rating4" name="rating" value="4">
                                <label for="rating4">⭐⭐⭐⭐ Very Good</label>
                            </div>
                            <div class="rating-option">
                                <input type="radio" id="rating5" name="rating" value="5">
                                <label for="rating5">⭐⭐⭐⭐⭐ Excellent</label>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="comments">Comments: <span style="color: red;">*</span></label>
                        <textarea id="comments" name="comments" required placeholder="Share your feedback..."></textarea>
                        <small>Please provide detailed feedback (1-1000 characters)</small>
                    </div>
                    
                    <div class="form-group">
                        <label for="feedbackType">Feedback Type: <span style="color: red;">*</span></label>
                        <select id="feedbackType" name="feedbackType" required>
                            <option value="">-- Select feedback type --</option>
                            <option value="GENERAL">General Feedback</option>
                            <option value="REQUEST_SPECIFIC">Request Specific</option>
                            <option value="SYSTEM_IMPROVEMENT">System Improvement</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="requestId">Related Request (Optional):</label>
                        <input type="text" id="requestId" name="requestId" placeholder="Enter request ID if applicable">
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn btn-submit">Submit Feedback</button>
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-cancel" style="text-decoration: none; text-align: center;">Cancel</a>
                    </div>
                </form>
            </div>
        </c:if>
        
        <!-- Feedback List (Admin Only) -->
        <c:if test="${param.action != 'new' && not empty feedbackList}">
            <div class="feedback-list">
                <c:forEach var="feedback" items="${feedbackList}">
                    <div class="feedback-item">
                        <div class="feedback-header-item">
                            <div>
                                <div class="feedback-user">User: ${feedback.userId}</div>
                                <div class="feedback-date">
                                    ${feedback.createdAt}
                                </div>
                            </div>
                            <div class="feedback-rating">
                                <c:forEach begin="1" end="${feedback.rating}">⭐</c:forEach>
                            </div>
                        </div>
                        
                        <div class="feedback-type">${feedback.feedbackType}</div>
                        
                        <c:if test="${not empty feedback.requestId}">
                            <div style="color: #666; font-size: 12px; margin-bottom: 10px;">
                                Request: ${feedback.requestId}
                            </div>
                        </c:if>
                        
                        <div class="feedback-comments">
                            ${feedback.comments}
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>
        
        <c:if test="${param.action != 'new' && empty feedbackList}">
            <div class="empty-state">
                <p>No feedback submitted yet</p>
                <a href="${pageContext.request.contextPath}/feedback?action=new" class="btn-new-feedback">
                    Submit Your Feedback
                </a>
            </div>
        </c:if>
    </div>
    
    <script>
        function validateFeedbackForm() {
            const rating = document.querySelector('input[name="rating"]:checked');
            const comments = document.getElementById('comments').value;
            const feedbackType = document.getElementById('feedbackType').value;
            
            if (!rating) {
                alert('Please select a rating');
                return false;
            }
            
            if (!comments || comments.trim().length === 0) {
                alert('Please provide comments');
                return false;
            }
            
            if (comments.length > 1000) {
                alert('Comments must be 1000 characters or less');
                return false;
            }
            
            if (!feedbackType) {
                alert('Please select a feedback type');
                return false;
            }
            
            return true;
        }
    </script>
</body>
</html>
