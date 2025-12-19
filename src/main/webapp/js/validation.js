/**
 * Client-side validation utilities
 * 
 * DESIGN DECISION: Client-side validation provides immediate feedback to users
 * without server round-trips. Server-side validation is still required for security.
 * This dual-layer approach improves UX while maintaining security.
 */

// Email validation regex
const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

// Username validation regex (3-20 chars, alphanumeric, underscore, hyphen)
const USERNAME_REGEX = /^[a-zA-Z0-9_-]{3,20}$/;

// Password validation requirements
const PASSWORD_MIN_LENGTH = 8;
const PASSWORD_UPPERCASE = /[A-Z]/;
const PASSWORD_LOWERCASE = /[a-z]/;
const PASSWORD_DIGIT = /\d/;
const PASSWORD_SPECIAL = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/;

// Centralized validation messages for consistency
const VALIDATION_MESSAGES = {
    REQUIRED: '%s is required',
    INVALID_EMAIL: 'Please enter a valid email address',
    INVALID_USERNAME: 'Username must be 3-20 characters (letters, numbers, underscore, hyphen)',
    PASSWORD_TOO_SHORT: 'Password must be at least 8 characters',
    PASSWORD_UPPERCASE: 'Password must contain at least one uppercase letter',
    PASSWORD_LOWERCASE: 'Password must contain at least one lowercase letter',
    PASSWORD_DIGIT: 'Password must contain at least one number',
    PASSWORD_SPECIAL: 'Password must contain at least one special character',
    PASSWORD_MISMATCH: 'Passwords do not match'
};

/**
 * Utility functions for form validation and feedback
 * WHY: Centralized error display ensures consistent UX across all forms
 */

function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
        errorElement.setAttribute('role', 'alert');
        // Announce to screen readers
        errorElement.setAttribute('aria-live', 'polite');
    }
}

function hideError(elementId) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.style.display = 'none';
        errorElement.removeAttribute('role');
        errorElement.removeAttribute('aria-live');
    }
}

/**
 * Shows a loading state on a button
 * WHY: Prevents double-submission and provides visual feedback
 */
function setButtonLoading(buttonId, isLoading) {
    const button = document.getElementById(buttonId);
    if (button) {
        if (isLoading) {
            button.disabled = true;
            button.setAttribute('data-original-text', button.textContent);
            button.textContent = 'Processing...';
            button.classList.add('btn-loading');
        } else {
            button.disabled = false;
            button.textContent = button.getAttribute('data-original-text') || 'Submit';
            button.classList.remove('btn-loading');
        }
    }
}

/**
 * Clears all error messages on a form
 * WHY: Provides clean slate when user starts correcting errors
 */
function clearFormErrors(formId) {
    const form = document.getElementById(formId);
    if (form) {
        const errorElements = form.querySelectorAll('.error-text');
        errorElements.forEach(element => {
            element.textContent = '';
            element.style.display = 'none';
        });
    }
}

function validateRequired(value, fieldName) {
    if (!value || value.trim() === '') {
        return `${fieldName} is required`;
    }
    return null;
}

function validateEmail(email) {
    if (!email) return 'Email is required';
    if (!EMAIL_REGEX.test(email)) {
        return 'Please enter a valid email address';
    }
    return null;
}

function validateUsername(username) {
    if (!username) return 'Username is required';
    if (!USERNAME_REGEX.test(username)) {
        if (username.length < 3) {
            return 'Username must be at least 3 characters long';
        } else if (username.length > 20) {
            return 'Username must be no more than 20 characters long';
        } else {
            return 'Username can only contain letters, numbers, underscores, and hyphens';
        }
    }
    return null;
}

function validatePassword(password) {
    if (!password) return 'Password is required';
    
    if (password.length < PASSWORD_MIN_LENGTH) {
        return `Password must be at least ${PASSWORD_MIN_LENGTH} characters long`;
    }
    
    if (!PASSWORD_UPPERCASE.test(password)) {
        return 'Password must contain at least one uppercase letter';
    }
    
    if (!PASSWORD_LOWERCASE.test(password)) {
        return 'Password must contain at least one lowercase letter';
    }
    
    if (!PASSWORD_DIGIT.test(password)) {
        return 'Password must contain at least one number';
    }
    
    if (!PASSWORD_SPECIAL.test(password)) {
        return 'Password must contain at least one special character';
    }
    
    return null;
}

function validatePasswordMatch(password, confirmPassword) {
    if (!confirmPassword) return 'Please confirm your password';
    if (password !== confirmPassword) {
        return 'Passwords do not match';
    }
    return null;
}

// Login form validation
function initializeLoginValidation() {
    const form = document.getElementById('loginForm');
    const usernameField = document.getElementById('username');
    const passwordField = document.getElementById('password');
    
    if (!form) return;
    
    // Real-time validation
    usernameField.addEventListener('blur', function() {
        const error = validateRequired(this.value, 'Username');
        if (error) {
            showError('usernameError', error);
        } else {
            hideError('usernameError');
        }
    });
    
    passwordField.addEventListener('blur', function() {
        const error = validateRequired(this.value, 'Password');
        if (error) {
            showError('passwordError', error);
        } else {
            hideError('passwordError');
        }
    });
    
    // Form submission validation
    form.addEventListener('submit', function(e) {
        let hasErrors = false;
        
        // Validate username
        const usernameError = validateRequired(usernameField.value, 'Username');
        if (usernameError) {
            showError('usernameError', usernameError);
            hasErrors = true;
        } else {
            hideError('usernameError');
        }
        
        // Validate password
        const passwordError = validateRequired(passwordField.value, 'Password');
        if (passwordError) {
            showError('passwordError', passwordError);
            hasErrors = true;
        } else {
            hideError('passwordError');
        }
        
        if (hasErrors) {
            e.preventDefault();
        }
    });
}

// Registration form validation
function initializeRegisterValidation() {
    const form = document.getElementById('registerForm');
    const usernameField = document.getElementById('username');
    const emailField = document.getElementById('email');
    const passwordField = document.getElementById('password');
    const confirmPasswordField = document.getElementById('confirmPassword');
    const roleField = document.getElementById('role');
    
    if (!form) return;
    
    // Real-time validation
    usernameField.addEventListener('blur', function() {
        const error = validateUsername(this.value);
        if (error) {
            showError('usernameError', error);
        } else {
            hideError('usernameError');
        }
    });
    
    emailField.addEventListener('blur', function() {
        const error = validateEmail(this.value);
        if (error) {
            showError('emailError', error);
        } else {
            hideError('emailError');
        }
    });
    
    passwordField.addEventListener('blur', function() {
        const error = validatePassword(this.value);
        if (error) {
            showError('passwordError', error);
        } else {
            hideError('passwordError');
        }
    });
    
    confirmPasswordField.addEventListener('blur', function() {
        const error = validatePasswordMatch(passwordField.value, this.value);
        if (error) {
            showError('confirmPasswordError', error);
        } else {
            hideError('confirmPasswordError');
        }
    });
    
    roleField.addEventListener('change', function() {
        const error = validateRequired(this.value, 'Role');
        if (error) {
            showError('roleError', error);
        } else {
            hideError('roleError');
        }
    });
    
    // Form submission validation
    form.addEventListener('submit', function(e) {
        let hasErrors = false;
        
        // Validate all fields
        const usernameError = validateUsername(usernameField.value);
        if (usernameError) {
            showError('usernameError', usernameError);
            hasErrors = true;
        } else {
            hideError('usernameError');
        }
        
        const emailError = validateEmail(emailField.value);
        if (emailError) {
            showError('emailError', emailError);
            hasErrors = true;
        } else {
            hideError('emailError');
        }
        
        const passwordError = validatePassword(passwordField.value);
        if (passwordError) {
            showError('passwordError', passwordError);
            hasErrors = true;
        } else {
            hideError('passwordError');
        }
        
        const confirmPasswordError = validatePasswordMatch(passwordField.value, confirmPasswordField.value);
        if (confirmPasswordError) {
            showError('confirmPasswordError', confirmPasswordError);
            hasErrors = true;
        } else {
            hideError('confirmPasswordError');
        }
        
        const roleError = validateRequired(roleField.value, 'Role');
        if (roleError) {
            showError('roleError', roleError);
            hasErrors = true;
        } else {
            hideError('roleError');
        }
        
        if (hasErrors) {
            e.preventDefault();
        }
    });
}

// Input sanitization
function sanitizeInput(input) {
    if (typeof input !== 'string') return input;
    
    return input
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#x27;')
        .replace(/&/g, '&amp;');
}

// AJAX helper function
function makeAjaxRequest(url, method, data, callback, errorCallback) {
    const xhr = new XMLHttpRequest();
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    callback(response);
                } catch (e) {
                    callback(xhr.responseText);
                }
            } else {
                if (errorCallback) {
                    errorCallback(xhr.status, xhr.statusText);
                }
            }
        }
    };
    
    xhr.open(method, url, true);
    
    if (method === 'POST' && data) {
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send(data);
    } else {
        xhr.send();
    }
}

// Form validation helper
function validateForm(formId, validationRules) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    let hasErrors = false;
    
    for (const fieldName in validationRules) {
        const field = form.querySelector(`[name="${fieldName}"]`);
        const rules = validationRules[fieldName];
        
        if (!field) continue;
        
        for (const rule of rules) {
            const error = rule(field.value);
            if (error) {
                showError(fieldName + 'Error', error);
                hasErrors = true;
                break;
            } else {
                hideError(fieldName + 'Error');
            }
        }
    }
    
    return !hasErrors;
}

/**
 * AJAX SEARCH FUNCTIONALITY
 * Real-time resource search with live result rendering
 * Demonstrates AJAX/JSON integration for Review-2 innovation requirement
 */

// Initialize AJAX search
function initializeAjaxSearch() {
    const searchInput = document.getElementById('searchInput');
    const searchResults = document.getElementById('searchResults');
    
    if (!searchInput) return;
    
    // Debounce timer for search
    let searchTimeout;
    
    searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        const query = this.value.trim();
        
        // Clear results if query is empty
        if (query.length === 0) {
            if (searchResults) {
                searchResults.innerHTML = '';
                searchResults.style.display = 'none';
            }
            return;
        }
        
        // Minimum 2 characters for search
        if (query.length < 2) {
            if (searchResults) {
                searchResults.innerHTML = '<div class="search-message">Type at least 2 characters to search</div>';
                searchResults.style.display = 'block';
            }
            return;
        }
        
        // Debounce search request (300ms delay)
        searchTimeout = setTimeout(function() {
            performAjaxSearch(query);
        }, 300);
    });
}

/**
 * Perform AJAX search request
 * WHY: Debounced search reduces server load and improves responsiveness
 * @param {string} query - Search query
 */
function performAjaxSearch(query) {
    const searchResults = document.getElementById('searchResults');
    
    if (!searchResults) return;
    
    // Show loading state with spinner
    searchResults.innerHTML = '<div class="search-loading"><span class="spinner"></span> Searching...</div>';
    searchResults.style.display = 'block';
    
    // Build URL with query parameter
    const url = '/community-hub/search?q=' + encodeURIComponent(query);
    
    // Make AJAX request
    const xhr = new XMLHttpRequest();
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    // Parse JSON response
                    const results = JSON.parse(xhr.responseText);
                    renderSearchResults(results, query);
                } catch (e) {
                    searchResults.innerHTML = '<div class="search-error">Error parsing search results</div>';
                }
            } else if (xhr.status === 401) {
                searchResults.innerHTML = '<div class="search-error">Please log in to search</div>';
            } else {
                searchResults.innerHTML = '<div class="search-error">Search failed. Please try again.</div>';
            }
        }
    };
    
    xhr.onerror = function() {
        searchResults.innerHTML = '<div class="search-error">Network error. Please try again.</div>';
    };
    
    xhr.open('GET', url, true);
    xhr.setRequestHeader('Accept', 'application/json');
    xhr.send();
}

/**
 * Render search results in the DOM
 * @param {Array} results - Array of resource objects from JSON response
 * @param {string} query - Original search query
 */
function renderSearchResults(results, query) {
    const searchResults = document.getElementById('searchResults');
    
    if (!searchResults) return;
    
    // Clear previous results
    searchResults.innerHTML = '';
    
    // Handle no results
    if (!results || results.length === 0) {
        searchResults.innerHTML = '<div class="search-no-results">No resources found matching "' + sanitizeInput(query) + '"</div>';
        searchResults.style.display = 'block';
        return;
    }
    
    // Create results container
    const resultsList = document.createElement('div');
    resultsList.className = 'search-results-list';
    
    // Render each result
    results.forEach(function(resource, index) {
        const resultItem = document.createElement('div');
        resultItem.className = 'search-result-item';
        
        // Highlight matching text
        const highlightedName = highlightSearchTerm(resource.name, query);
        const highlightedDesc = highlightSearchTerm(resource.description || '', query);
        
        resultItem.innerHTML = `
            <div class="result-name">${highlightedName}</div>
            <div class="result-category">${sanitizeInput(resource.category)}</div>
            <div class="result-description">${highlightedDesc}</div>
            <div class="result-meta">
                <span class="result-quantity">Available: ${resource.quantity}</span>
                <span class="result-location">${sanitizeInput(resource.location || 'N/A')}</span>
            </div>
        `;
        
        // Add click handler to select resource
        resultItem.addEventListener('click', function() {
            selectSearchResult(resource);
        });
        
        resultsList.appendChild(resultItem);
    });
    
    searchResults.appendChild(resultsList);
    searchResults.style.display = 'block';
}

/**
 * Highlight search term in text
 * @param {string} text - Text to highlight
 * @param {string} term - Term to highlight
 * @returns {string} HTML with highlighted term
 */
function highlightSearchTerm(text, term) {
    if (!text) return '';
    
    const regex = new RegExp('(' + term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'gi');
    return sanitizeInput(text).replace(regex, '<strong>$1</strong>');
}

/**
 * Handle search result selection
 * @param {Object} resource - Selected resource object
 */
function selectSearchResult(resource) {
    // Populate resource field if it exists
    const resourceField = document.getElementById('resourceId');
    if (resourceField) {
        resourceField.value = resource.resourceId;
        
        // Trigger change event for form validation
        const event = new Event('change', { bubbles: true });
        resourceField.dispatchEvent(event);
    }
    
    // Clear search results
    const searchResults = document.getElementById('searchResults');
    if (searchResults) {
        searchResults.innerHTML = '';
        searchResults.style.display = 'none';
    }
    
    // Clear search input
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.value = '';
    }
}

// Initialize validation on page load
document.addEventListener('DOMContentLoaded', function() {
    // Clear all error messages on page load
    const errorElements = document.querySelectorAll('.error-text');
    errorElements.forEach(element => {
        element.style.display = 'none';
    });
    
    // Add input event listeners to clear errors when user starts typing
    const inputs = document.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            const errorId = this.name + 'Error';
            hideError(errorId);
        });
    });
    
    // Initialize AJAX search
    initializeAjaxSearch();
});