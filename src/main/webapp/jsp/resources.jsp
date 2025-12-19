<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ResoMap - Resources</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=5.0">
</head>
<body>
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
                        <input type="text" id="searchInput" class="search-input" placeholder="Search resources...">
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
            
            <div class="category-filter-section">
                <button class="category-btn active" data-category="ALL">All Resources</button>
                <button class="category-btn" data-category="FOOD">🍎 Food</button>
                <button class="category-btn" data-category="CLOTHING">👕 Clothing</button>
                <button class="category-btn" data-category="SHELTER">🏠 Shelter</button>
                <button class="category-btn" data-category="MEDICAL">⚕️ Medical</button>
                <button class="category-btn" data-category="EDUCATION">📚 Education</button>
                <button class="category-btn" data-category="OTHER">📦 Other</button>
            </div>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error"><span>${error}</span></div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success"><span>${success}</span></div>
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
            
            <c:if test="${param.action == 'edit' && sessionScope.user.role == 'ADMIN' && not empty param.id}">
                <div class="section mb-lg">
                    <div class="section-header">
                        <h2 class="section-title">Edit Resource</h2>
                    </div>
                    <c:set var="editResource" value="${null}"/>
                    <c:forEach var="resource" items="${resources}">
                        <c:if test="${resource.resourceId == param.id}">
                            <c:set var="editResource" value="${resource}"/>
                        </c:if>
                    </c:forEach>
                    
                    <c:if test="${not empty editResource}">
                        <form action="${pageContext.request.contextPath}/resources" method="post">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${editResource.resourceId}">
                            <div class="form-group">
                                <label for="editName">Resource Name:</label>
                                <input type="text" id="editName" name="name" value="${editResource.name}" required>
                            </div>
                            <div class="form-group">
                                <label for="editDescription">Description:</label>
                                <textarea id="editDescription" name="description" rows="3">${editResource.description}</textarea>
                            </div>
                            <div class="form-group">
                                <label for="editCategory">Category:</label>
                                <select id="editCategory" name="category" required>
                                    <option value="FOOD" <c:if test="${editResource.category == 'FOOD'}">selected</c:if>>Food</option>
                                    <option value="CLOTHING" <c:if test="${editResource.category == 'CLOTHING'}">selected</c:if>>Clothing</option>
                                    <option value="SHELTER" <c:if test="${editResource.category == 'SHELTER'}">selected</c:if>>Shelter</option>
                                    <option value="MEDICAL" <c:if test="${editResource.category == 'MEDICAL'}">selected</c:if>>Medical</option>
                                    <option value="EDUCATION" <c:if test="${editResource.category == 'EDUCATION'}">selected</c:if>>Education</option>
                                    <option value="OTHER" <c:if test="${editResource.category == 'OTHER'}">selected</c:if>>Other</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="editQuantity">Quantity:</label>
                                <input type="number" id="editQuantity" name="quantity" value="${editResource.quantity}" min="0" required>
                            </div>
                            <div class="form-group">
                                <label for="editLocation">Location:</label>
                                <input type="text" id="editLocation" name="location" value="${editResource.location}">
                            </div>
                            <div class="form-group">
                                <label for="editContactInfo">Contact Info:</label>
                                <input type="text" id="editContactInfo" name="contactInfo" value="${editResource.contactInfo}">
                            </div>
                            <button type="submit" class="btn btn-primary">Update Resource</button>
                            <a href="${pageContext.request.contextPath}/resources" class="btn btn-secondary">Cancel</a>
                        </form>
                    </c:if>
                    <c:if test="${empty editResource}">
                        <div class="alert alert-error">
                            <span>Resource not found</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/resources" class="btn btn-secondary">Back to Resources</a>
                    </c:if>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${empty resources}">
                    <div class="empty-state-container">
                        <div class="empty-state">
                            <div class="empty-state-icon">📦</div>
                            <div class="empty-state-title">No Resources Found</div>
                            <div class="empty-state-text">
                                <c:if test="${sessionScope.user.role == 'ADMIN'}">Start by adding your first resource to help the community.</c:if>
                                <c:if test="${sessionScope.user.role != 'ADMIN'}">No resources are currently available. Check back soon!</c:if>
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
                            <div class="resource-card" data-category="${resource.category}">
                                <div class="resource-card-accent"></div>
                                <div class="resource-card-header">
                                    <div class="resource-header-top">
                                        <h3 class="resource-card-title">${resource.name}</h3>
                                        <span class="resource-card-category">${resource.category}</span>
                                    </div>
                                </div>
                                <div class="resource-card-body">
                                    <p class="resource-description">${resource.description}</p>
                                    <div class="resource-quantity-section">
                                        <div class="quantity-label">Available Quantity</div>
                                        <div class="quantity-display">
                                            <span class="quantity-value">${resource.quantity}</span>
                                            <div class="quantity-indicator ${resource.quantity > 10 ? 'high' : resource.quantity > 5 ? 'medium' : 'low'}"></div>
                                        </div>
                                    </div>
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
                                        <a href="${pageContext.request.contextPath}/resources?action=delete&id=${resource.resourceId}" class="action-btn action-delete" onclick="return confirm('Are you sure?')">
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

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var categoryBtns = document.querySelectorAll('.category-btn');
            var searchInput = document.getElementById('searchInput');
            var resourceCards = document.querySelectorAll('.resource-card');
            var currentCategory = 'ALL';
            var currentSearch = '';

            function filterCards() {
                var visibleCount = 0;
                resourceCards.forEach(function(card) {
                    var category = card.getAttribute('data-category');
                    var name = card.textContent.toLowerCase();
                    
                    var categoryMatch = (currentCategory === 'ALL') || (category === currentCategory);
                    var searchMatch = !currentSearch || name.indexOf(currentSearch) > -1;
                    
                    if (categoryMatch && searchMatch) {
                        card.style.display = '';
                        visibleCount++;
                    } else {
                        card.style.display = 'none';
                    }
                });
                
                var grid = document.getElementById('resourcesGrid');
                var noResults = document.getElementById('noResultsMsg');
                if (visibleCount === 0 && grid) {
                    if (!noResults) {
                        noResults = document.createElement('div');
                        noResults.id = 'noResultsMsg';
                        noResults.style.cssText = 'text-align:center;padding:40px;color:#999;';
                        noResults.textContent = 'No resources match your filters.';
                        grid.parentNode.insertBefore(noResults, grid.nextSibling);
                    }
                    noResults.style.display = 'block';
                } else if (noResults) {
                    noResults.style.display = 'none';
                }
            }

            categoryBtns.forEach(function(btn) {
                btn.addEventListener('click', function() {
                    categoryBtns.forEach(function(b) { b.classList.remove('active'); });
                    btn.classList.add('active');
                    currentCategory = btn.getAttribute('data-category');
                    filterCards();
                });
            });

            if (searchInput) {
                searchInput.addEventListener('keyup', function() {
                    currentSearch = searchInput.value.toLowerCase().trim();
                    filterCards();
                });
            }
        });
    </script>
</body>
</html>
