<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ResoMap - Resources</title>
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
                <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-menu-link" title="Dashboard">
                    <span class="sidebar-menu-icon">📊</span>
                    <span class="sidebar-menu-label">Dashboard</span>
                </a>
            </li>
            <li class="sidebar-menu-item">
                <a href="${pageContext.request.contextPath}/resources" class="sidebar-menu-link active" title="Resources">
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
            <div class="resources-page-header">
                <div class="resources-header-content">
                    <h1 class="page-title">Resources</h1>
                    <p class="page-subtitle">Manage and track available community resources</p>
                </div>
                <div class="resources-header-actions">
                    <div class="search-container">
                        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="11" cy="11" r="8"></circle>
                            <path d="m21 21-4.35-4.35"></path>
                        </svg>
                        <input type="text" id="searchInput" class="search-input" placeholder="Search resources..." onkeyup="searchResources()" oninput="searchResources()">
                        <div id="searchResults" class="search-results"></div>
                    </div>
                    <c:if test="${sessionScope.user.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/resources?action=create" class="btn btn-primary btn-with-icon">
                            <span class="btn-icon">➕</span>
                            <span class="btn-label">Add Resource</span>
                        </a>
                    </c:if>
                </div>
            </div>
            
            <!-- Category Filter -->
            <div class="category-filter-section">
                <button class="category-btn active" onclick="filterByCategory('ALL')">All Resources</button>
                <button class="category-btn" onclick="filterByCategory('FOOD')">🍎 Food</button>
                <button class="category-btn" onclick="filterByCategory('CLOTHING')">👕 Clothing</button>
                <button class="category-btn" onclick="filterByCategory('SHELTER')">🏠 Shelter</button>
                <button class="category-btn" onclick="filterByCategory('MEDICAL')">⚕️ Medical</button>
                <button class="category-btn" onclick="filterByCategory('EDUCATION')">📚 Education</button>
                <button class="category-btn" onclick="filterByCategory('OTHER')">📦 Other</button>
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
            
            <c:if test="${param.action == 'create' && sessionScope.user.role == 'ADMIN'}">
                <div class="section mb-lg">
                    <div class="section-header">
                        <h2 class="section-title">Add New Resource</h2>
                    </div>
                    <form action="${pageContext.request.contextPath}/resources" method="post">
                        <input type="hidden" name="action" value="create">
                        
                        <div class="form-group">
                            <label for="name">Resource Name:</label>
                            <input type="text" id="name" name="name" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="description">Description:</label>
                            <textarea id="description" name="description" rows="3"></textarea>
                        </div>
                        
                        <div class="form-group">
                            <label for="category">Category:</label>
                            <select id="category" name="category" required>
                                <option value="">Select Category</option>
                                <option value="FOOD">Food</option>
                                <option value="CLOTHING">Clothing</option>
                                <option value="SHELTER">Shelter</option>
                                <option value="MEDICAL">Medical</option>
                                <option value="EDUCATION">Education</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="quantity">Quantity:</label>
                            <input type="number" id="quantity" name="quantity" min="0" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="location">Location:</label>
                            <input type="text" id="location" name="location">
                        </div>
                        
                        <div class="form-group">
                            <label for="contactInfo">Contact Info:</label>
                            <input type="text" id="contactInfo" name="contactInfo">
                        </div>
                        
                        <button type="submit" class="btn btn-primary">Add Resource</button>
                        <a href="${pageContext.request.contextPath}/resources" class="btn btn-secondary">Cancel</a>
                    </form>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${empty resources}">
                    <div class="empty-state-container">
                        <div class="empty-state">
                            <div class="empty-state-icon">📦</div>
                            <div class="empty-state-title">No Resources Found</div>
                            <div class="empty-state-text">
                                <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                    Start by adding your first resource to help the community.
                                </c:if>
                                <c:if test="${sessionScope.user.role != 'ADMIN'}">
                                    No resources are currently available. Check back soon!
                                </c:if>
                            </div>
                            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                <a href="${pageContext.request.contextPath}/resources?action=create" class="btn btn-primary mt-lg">Add First Resource</a>
                            </c:if>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="resources-grid mb-lg" id="resourcesGrid">
                        <c:forEach var="resource" items="${resources}">
                            <div class="resource-card" data-resource-id="${resource.resourceId}" data-category="${resource.category}" data-name="${resource.name}" data-description="${resource.description}" data-location="${resource.location}">
                                <!-- Accent Strip -->
                                <div class="resource-card-accent"></div>
                                
                                <!-- Header with Category Badge -->
                                <div class="resource-card-header">
                                    <div class="resource-header-top">
                                        <h3 class="resource-card-title">${resource.name}</h3>
                                        <span class="resource-card-category">${resource.category}</span>
                                    </div>
                                </div>
                                
                                <!-- Body Content -->
                                <div class="resource-card-body">
                                    <p class="resource-description">${resource.description}</p>
                                    
                                    <!-- Quantity Indicator -->
                                    <div class="resource-quantity-section">
                                        <div class="quantity-label">Available Quantity</div>
                                        <div class="quantity-display">
                                            <span class="quantity-value">${resource.quantity}</span>
                                            <div class="quantity-indicator ${resource.quantity > 10 ? 'high' : resource.quantity > 5 ? 'medium' : 'low'}"></div>
                                        </div>
                                    </div>
                                    
                                    <!-- Metadata Grid -->
                                    <div class="resource-meta">
                                        <div class="resource-meta-item">
                                            <div class="resource-meta-label">📍 Location</div>
                                            <div class="resource-meta-value">${resource.location}</div>
                                        </div>
                                        <div class="resource-meta-item">
                                            <div class="resource-meta-label">📞 Contact</div>
                                            <div class="resource-meta-value">${resource.contactInfo}</div>
                                        </div>
                                    </div>
                                </div>
                                
                                <!-- Action Bar -->
                                <div class="resource-card-actions">
                                    <c:if test="${sessionScope.user.role == 'REQUESTER'}">
                                        <a href="${pageContext.request.contextPath}/requests?action=create&resourceId=${resource.resourceId}" class="action-btn action-primary">
                                            <span class="action-icon">📝</span>
                                            <span class="action-label">Request</span>
                                        </a>
                                    </c:if>
                                    <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                        <a href="${pageContext.request.contextPath}/resources?action=edit&id=${resource.resourceId}" class="action-btn action-edit">
                                            <span class="action-icon">✏️</span>
                                            <span class="action-label">Edit</span>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/resources?action=delete&id=${resource.resourceId}" class="action-btn action-delete" onclick="return confirm('Are you sure you want to delete this resource?')">
                                            <span class="action-icon">🗑️</span>
                                            <span class="action-label">Delete</span>
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/validation.js"></script>
    <script>
        let currentCategory = 'ALL';
        
        // Store all resources data for client-side search
        const allResources = [
            <c:forEach var="resource" items="${resources}" varStatus="status">
                {
                    resourceId: '${resource.resourceId}',
                    name: '${resource.name}',
                    category: '${resource.category}',
                    description: '${resource.description}',
                    quantity: ${resource.quantity},
                    location: '${resource.location}',
                    contactInfo: '${resource.contactInfo}'
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        function searchResources() {
            const searchInput = document.getElementById('searchInput');
            const searchTerm = searchInput.value.toLowerCase().trim();
            const resultsContainer = document.getElementById('searchResults');
            
            // Clear results if search term is too short
            if (searchTerm.length < 1) {
                resultsContainer.innerHTML = '';
                return;
            }
            
            // Filter resources based on search term
            const filteredResources = allResources.filter(resource => {
                const name = (resource.name || '').toLowerCase();
                const category = (resource.category || '').toLowerCase();
                const description = (resource.description || '').toLowerCase();
                const location = (resource.location || '').toLowerCase();
                
                return name.includes(searchTerm) ||
                       category.includes(searchTerm) ||
                       description.includes(searchTerm) ||
                       location.includes(searchTerm);
            });
            
            displaySearchResults(filteredResources);
        }
        
        function displaySearchResults(results) {
            const resultsContainer = document.getElementById('searchResults');
            
            if (results.length === 0) {
                resultsContainer.innerHTML = '<div style="padding: 12px 16px; color: var(--gray-500); text-align: center; font-size: 13px;">No resources found</div>';
                return;
            }
            
            let html = '<ul style="list-style: none; margin: 0; padding: 0;">';
            results.forEach(resource => {
                const safeId = resource.resourceId.replace(/'/g, "\\'");
                html += `<li onclick="selectResource('${safeId}')" style="padding: 10px 12px; border-bottom: 1px solid #eee; cursor: pointer; transition: background 0.2s;">
                    <strong style="color: #333;">${resource.name}</strong> <span style="color: #999; font-size: 12px;">• ${resource.category}</span>
                    <br><small style="color: #666;">${resource.description}</small>
                </li>`;
            });
            html += '</ul>';
            resultsContainer.innerHTML = html;
        }
        
        function selectResource(resourceId) {
            // Clear search
            document.getElementById('searchInput').value = '';
            document.getElementById('searchResults').innerHTML = '';
            
            // Scroll to resource card
            const resourceCard = document.querySelector('[data-resource-id="' + resourceId + '"]');
            if (resourceCard) {
                resourceCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
                resourceCard.style.boxShadow = '0 0 0 3px rgba(37, 99, 235, 0.3)';
                setTimeout(() => {
                    resourceCard.style.boxShadow = '';
                }, 2000);
            }
        }
        
        function filterByCategory(category) {
            currentCategory = category;
            
            // Update active button - find the button that was clicked
            document.querySelectorAll('.category-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            
            // Find and activate the correct button
            const buttons = document.querySelectorAll('.category-btn');
            buttons.forEach(btn => {
                const btnText = btn.textContent.trim();
                if ((category === 'ALL' && btnText === 'All Resources') ||
                    (category === 'FOOD' && btnText.includes('Food')) ||
                    (category === 'CLOTHING' && btnText.includes('Clothing')) ||
                    (category === 'SHELTER' && btnText.includes('Shelter')) ||
                    (category === 'MEDICAL' && btnText.includes('Medical')) ||
                    (category === 'EDUCATION' && btnText.includes('Education')) ||
                    (category === 'OTHER' && btnText.includes('Other'))) {
                    btn.classList.add('active');
                }
            });
            
            // Filter cards
            const cards = document.querySelectorAll('.resource-card');
            cards.forEach(card => {
                const cardCategory = card.getAttribute('data-category');
                if (category === 'ALL' || cardCategory === category) {
                    card.style.display = '';
                    card.classList.add('fade-in');
                } else {
                    card.style.display = 'none';
                }
            });
        }
        
        // Close search results when clicking outside
        document.addEventListener('click', function(event) {
            const searchContainer = document.querySelector('.search-container');
            if (searchContainer && !searchContainer.contains(event.target)) {
                document.getElementById('searchResults').innerHTML = '';
            }
        });
        
        // Add fade-in animation
        const style = document.createElement('style');
        style.textContent = `
            @keyframes fadeIn {
                from { opacity: 0; transform: translateY(10px); }
                to { opacity: 1; transform: translateY(0); }
            }
            .resource-card.fade-in {
                animation: fadeIn 0.3s ease-out;
            }
        `;
        document.head.appendChild(style);
    </script>
</body>
</html>