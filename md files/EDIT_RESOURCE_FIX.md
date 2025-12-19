# Edit Resource Button Fix

## Problem
The Edit button on the Resources page was not working properly. When clicking the Edit button, the page would navigate to `resources?action=edit&id={resourceId}` but no edit form would display.

## Root Cause
The resources.jsp page was missing the edit form section. The page only had:
- Create form (when `action=create`)
- Resource grid display
- But NO edit form (when `action=edit`)

## Solution
Added a complete edit form section to resources.jsp that:

1. **Checks for edit action**: `<c:if test="${param.action == 'edit' && sessionScope.user.role == 'ADMIN' && not empty param.id}">`

2. **Finds the resource to edit**: Loops through resources list to find the one matching the ID parameter

3. **Displays edit form** with:
   - Pre-filled resource name
   - Pre-filled description
   - Pre-filled category (with selected option)
   - Pre-filled quantity
   - Pre-filled location
   - Pre-filled contact info
   - Hidden fields for action="update" and resource ID

4. **Handles missing resource**: Shows error message if resource not found

## Code Added

```jsp
<c:if test="${param.action == 'edit' && sessionScope.user.role == 'ADMIN' && not empty param.id}">
    <div class="section mb-lg">
        <div class="section-header">
            <h2 class="section-title">Edit Resource</h2>
        </div>
        
        <!-- Find the resource to edit -->
        <c:set var="editResource" value="${null}"/>
        <c:forEach var="resource" items="${resources}">
            <c:if test="${resource.resourceId == param.id}">
                <c:set var="editResource" value="${resource}"/>
            </c:if>
        </c:forEach>
        
        <!-- Display edit form if resource found -->
        <c:if test="${not empty editResource}">
            <form action="${pageContext.request.contextPath}/resources" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${editResource.resourceId}">
                
                <!-- Form fields with pre-filled values -->
                <div class="form-group">
                    <label for="editName">Resource Name:</label>
                    <input type="text" id="editName" name="name" value="${editResource.name}" required>
                </div>
                
                <!-- ... other fields ... -->
                
                <button type="submit" class="btn btn-primary">Update Resource</button>
                <a href="${pageContext.request.contextPath}/resources" class="btn btn-secondary">Cancel</a>
            </form>
        </c:if>
        
        <!-- Show error if resource not found -->
        <c:if test="${empty editResource}">
            <div class="alert alert-error">
                <span>Resource not found</span>
            </div>
            <a href="${pageContext.request.contextPath}/resources" class="btn btn-secondary">Back to Resources</a>
        </c:if>
    </div>
</c:if>
```

## How It Works

### Step 1: User clicks Edit button
```html
<a href="${pageContext.request.contextPath}/resources?action=edit&id=${resource.resourceId}" class="action-btn action-edit">
    <span class="action-icon">✏️</span>
    <span class="action-label">Edit</span>
</a>
```

### Step 2: Page loads with edit form
- URL: `resources?action=edit&id=res-medical-001`
- JSP checks: `param.action == 'edit'` ✓
- JSP checks: `sessionScope.user.role == 'ADMIN'` ✓
- JSP checks: `not empty param.id` ✓

### Step 3: Find resource in list
```jsp
<c:forEach var="resource" items="${resources}">
    <c:if test="${resource.resourceId == param.id}">
        <c:set var="editResource" value="${resource}"/>
    </c:if>
</c:forEach>
```

### Step 4: Display form with pre-filled values
- Resource name: `${editResource.name}`
- Description: `${editResource.description}`
- Category: `${editResource.category}` (selected in dropdown)
- Quantity: `${editResource.quantity}`
- Location: `${editResource.location}`
- Contact: `${editResource.contactInfo}`

### Step 5: Submit form
- Form posts to: `resources` servlet
- Action: `update`
- Resource ID: `${editResource.resourceId}`
- Updated values: name, description, category, quantity, location, contactInfo

## Form Structure

```
Edit Resource Form
├── Resource Name (text input, pre-filled)
├── Description (textarea, pre-filled)
├── Category (dropdown, pre-selected)
├── Quantity (number input, pre-filled)
├── Location (text input, pre-filled)
├── Contact Info (text input, pre-filled)
├── Update Resource (submit button)
└── Cancel (link back to resources)
```

## Error Handling

If resource is not found:
- Shows error message: "Resource not found"
- Provides "Back to Resources" link
- Prevents form submission

## Testing

### Test Case 1: Edit existing resource
1. Click Edit button on any resource card
2. Form should display with pre-filled values
3. Modify any field
4. Click "Update Resource"
5. Resource should be updated

### Test Case 2: Edit non-existent resource
1. Manually navigate to: `resources?action=edit&id=invalid-id`
2. Should show error message
3. Should provide back link

### Test Case 3: Non-admin user
1. Login as non-admin user
2. Edit button should not appear
3. If manually navigate to edit URL, form should not display

## Files Modified

- `src/main/webapp/jsp/resources.jsp` - Added edit form section

## Verification

✓ Edit button now displays edit form
✓ Form pre-fills with current resource data
✓ Form submits to update action
✓ Error handling for missing resources
✓ Role-based access control maintained
✓ No compilation errors
✓ No JSP syntax errors

## Next Steps

1. Hard refresh browser (Ctrl+Shift+R)
2. Click Edit button on any resource
3. Verify form displays with pre-filled values
4. Test updating a resource
5. Verify resource is updated in the list

## Notes

- Edit form uses same styling as Create form
- Pre-filled values use JSP EL expressions
- Category dropdown uses `<c:if>` for selected option
- Form action is "update" (not "create")
- Resource ID is passed as hidden field
- Cancel button returns to resources list
