<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Request - Community Resource Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=5.0">
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
            <span class="sidebar-footer-role">${sessionScope.user.role}</span>
        </div>
    </aside>
    
    <!-- Main Content -->
    <div class="main-layout">
        <main class="main-content">
            <div class="request-form-header">
                <h1 class="page-title">Create New Request</h1>
                <p class="page-subtitle">Tell us what resource you need and we'll help connect you</p>
            </div>
            
            <div class="request-form-container">
                <c:if test="${not empty error}">
                    <div class="alert alert-error">
                        <span class="alert-icon">⚠️</span>
                        <span>${error}</span>
                    </div>
                </c:if>
            
                <form method="POST" action="${pageContext.request.contextPath}/requests" onsubmit="return validateForm()" class="request-form">
                    <input type="hidden" name="action" value="create">
                    
                    <!-- Step 1: Resource Selection -->
                    <div class="form-section">
                        <div class="form-section-header">
                            <div class="form-step-number">1</div>
                            <div>
                                <h2 class="form-section-title">Select a Resource</h2>
                                <p class="form-section-subtitle">What resource do you need?</p>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="resourceSearch" class="form-label">
                                Search Resources
                                <span class="required-indicator">*</span>
                            </label>
                            <div class="resource-search-wrapper">
                                <input 
                                    type="text" 
                                    id="resourceSearch" 
                                    class="resource-search-input" 
                                    placeholder="Type resource name or category..." 
                                    autocomplete="off"
                                    aria-label="Search resources"
                                >
                                <div id="searchResults" class="search-results-dropdown"></div>
                            </div>
                            <div id="resourceError" class="form-error"></div>
                        </div>
                        
                        <div class="form-group">
                            <label for="resourceId" class="form-label">
                                Selected Resource
                                <span class="required-indicator">*</span>
                            </label>
                            <div class="resource-display">
                                <select id="resourceId" name="resourceId" required class="form-select" aria-label="Selected resource">
                                    <option value="">-- Choose a resource --</option>
                                    <c:forEach var="resource" items="${resources}">
                                        <option value="${resource.resourceId}" data-name="${resource.name}" data-quantity="${resource.quantity}">
                                            ${resource.name} (${resource.quantity} available)
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div id="selectedResourceInfo" class="resource-info-card" style="display: none;">
                                <div class="resource-info-name" id="selectedResourceName"></div>
                                <div class="resource-info-quantity" id="selectedResourceQuantity"></div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Step 2: Urgency Selection -->
                    <div class="form-section">
                        <div class="form-section-header">
                            <div class="form-step-number">2</div>
                            <div>
                                <h2 class="form-section-title">How Urgent?</h2>
                                <p class="form-section-subtitle">Select the urgency level for your request</p>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label class="form-label">
                                Urgency Level
                                <span class="required-indicator">*</span>
                            </label>
                            <div class="urgency-selector">
                                <label class="urgency-option">
                                    <input type="radio" name="urgencyLevel" value="LOW" required aria-label="Low urgency">
                                    <div class="urgency-card urgency-low">
                                        <div class="urgency-icon">🟢</div>
                                        <div class="urgency-label">Low</div>
                                        <div class="urgency-description">Can wait, no immediate need</div>
                                    </div>
                                </label>
                                
                                <label class="urgency-option">
                                    <input type="radio" name="urgencyLevel" value="MEDIUM" required aria-label="Medium urgency">
                                    <div class="urgency-card urgency-medium">
                                        <div class="urgency-icon">🟡</div>
                                        <div class="urgency-label">Medium</div>
                                        <div class="urgency-description">Needed within a few days</div>
                                    </div>
                                </label>
                                
                                <label class="urgency-option">
                                    <input type="radio" name="urgencyLevel" value="HIGH" required aria-label="High urgency">
                                    <div class="urgency-card urgency-high">
                                        <div class="urgency-icon">🟠</div>
                                        <div class="urgency-label">High</div>
                                        <div class="urgency-description">Urgent, needed within 24 hours</div>
                                    </div>
                                </label>
                                
                                <label class="urgency-option">
                                    <input type="radio" name="urgencyLevel" value="CRITICAL" required aria-label="Critical urgency">
                                    <div class="urgency-card urgency-critical">
                                        <div class="urgency-icon">🔴</div>
                                        <div class="urgency-label">Critical</div>
                                        <div class="urgency-description">Emergency, immediate attention</div>
                                    </div>
                                </label>
                            </div>
                            <div id="urgencyError" class="form-error"></div>
                        </div>
                    </div>
                    
                    <!-- Step 3: Description -->
                    <div class="form-section">
                        <div class="form-section-header">
                            <div class="form-step-number">3</div>
                            <div>
                                <h2 class="form-section-title">Tell Us More</h2>
                                <p class="form-section-subtitle">Provide details about your request</p>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="description" class="form-label">
                                Description
                                <span class="required-indicator">*</span>
                            </label>
                            <div class="textarea-wrapper">
                                <textarea 
                                    id="description" 
                                    name="description" 
                                    required 
                                    class="form-textarea"
                                    placeholder="Describe what you need and why. For example: 'I need winter clothing for my family of 4. We have limited budget and need warm jackets and boots.'"
                                    maxlength="500"
                                    aria-label="Request description"
                                ></textarea>
                                <div class="textarea-footer">
                                    <span id="charCount" class="char-counter">0 / 500</span>
                                </div>
                            </div>
                            <div id="descriptionError" class="form-error"></div>
                        </div>
                    </div>
                    
                    <!-- Action Buttons -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
                            <span class="btn-icon">✓</span>
                            <span class="btn-label">Submit Request</span>
                        </button>
                        <a href="${pageContext.request.contextPath}/requests" class="btn btn-secondary btn-lg">
                            <span class="btn-label">Cancel</span>
                        </a>
                    </div>
                </form>
            </div>
        </main>
    </div>
    
    <script>
        // Character counter for description
        document.getElementById('description').addEventListener('input', function() {
            const count = this.value.length;
            document.getElementById('charCount').textContent = count + ' / 500';
            
            if (count > 500) {
                this.value = this.value.substring(0, 500);
                document.getElementById('charCount').textContent = '500 / 500';
            }
        });
        
        // Resource selection display
        document.getElementById('resourceId').addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            const infoCard = document.getElementById('selectedResourceInfo');
            
            if (this.value) {
                document.getElementById('selectedResourceName').textContent = selectedOption.dataset.name;
                document.getElementById('selectedResourceQuantity').textContent = 
                    '📦 ' + selectedOption.dataset.quantity + ' available';
                infoCard.style.display = 'block';
                clearError('resourceError');
            } else {
                infoCard.style.display = 'none';
            }
        });
        
        // Resource search functionality
        const allResources = [
            <c:forEach var="resource" items="${resources}" varStatus="status">
                {
                    id: '${resource.resourceId}',
                    name: '${resource.name}',
                    category: '${resource.category}',
                    quantity: ${resource.quantity}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        
        document.getElementById('resourceSearch').addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase().trim();
            const resultsDiv = document.getElementById('searchResults');
            
            if (searchTerm.length < 1) {
                resultsDiv.innerHTML = '';
                return;
            }
            
            const filtered = allResources.filter(r => 
                r.name.toLowerCase().includes(searchTerm) ||
                r.category.toLowerCase().includes(searchTerm)
            );
            
            if (filtered.length === 0) {
                resultsDiv.innerHTML = '<div class="search-result-item no-results">No resources found</div>';
                return;
            }
            
            resultsDiv.innerHTML = filtered.map(r => 
                `<div class="search-result-item" onclick="selectResource('${r.id}', '${r.name}')">
                    <div class="search-result-name">${r.name}</div>
                    <div class="search-result-meta">${r.category} • ${r.quantity} available</div>
                </div>`
            ).join('');
        });
        
        function selectResource(resourceId, resourceName) {
            document.getElementById('resourceId').value = resourceId;
            document.getElementById('resourceSearch').value = '';
            document.getElementById('searchResults').innerHTML = '';
            
            // Trigger change event to update display
            document.getElementById('resourceId').dispatchEvent(new Event('change'));
        }
        
        // Close search results when clicking outside
        document.addEventListener('click', function(e) {
            if (!e.target.closest('.resource-search-wrapper')) {
                document.getElementById('searchResults').innerHTML = '';
            }
        });
        
        // Form validation with inline errors
        function validateForm() {
            let isValid = true;
            
            // Clear all errors
            document.querySelectorAll('.form-error').forEach(el => el.textContent = '');
            
            // Validate resource
            const resourceId = document.getElementById('resourceId').value;
            if (!resourceId) {
                showError('resourceError', 'Please select a resource');
                isValid = false;
            }
            
            // Validate urgency
            const urgencyLevel = document.querySelector('input[name="urgencyLevel"]:checked');
            if (!urgencyLevel) {
                showError('urgencyError', 'Please select an urgency level');
                isValid = false;
            }
            
            // Validate description
            const description = document.getElementById('description').value;
            if (!description || description.trim().length === 0) {
                showError('descriptionError', 'Please provide a description');
                isValid = false;
            } else if (description.length > 500) {
                showError('descriptionError', 'Description must be 500 characters or less');
                isValid = false;
            } else if (description.trim().length < 10) {
                showError('descriptionError', 'Description must be at least 10 characters');
                isValid = false;
            }
            
            return isValid;
        }
        
        function showError(elementId, message) {
            const errorEl = document.getElementById(elementId);
            if (errorEl) {
                errorEl.textContent = message;
            }
        }
        
        function clearError(elementId) {
            const errorEl = document.getElementById(elementId);
            if (errorEl) {
                errorEl.textContent = '';
            }
        }
        
        // Enable/disable submit button based on form state
        const form = document.querySelector('.request-form');
        const submitBtn = document.getElementById('submitBtn');
        
        form.addEventListener('change', function() {
            const resourceId = document.getElementById('resourceId').value;
            const urgencyLevel = document.querySelector('input[name="urgencyLevel"]:checked');
            const description = document.getElementById('description').value.trim();
            
            const isComplete = resourceId && urgencyLevel && description.length >= 10;
            submitBtn.disabled = !isComplete;
        });
        
        // Initial state
        submitBtn.disabled = true;
    </script>
</body>
</html>
