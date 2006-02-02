<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ include file="adminHeader.jsp" %>
<%@ include file="functionMenu.jsp" %>

<portlet:actionURL var="createResourceUploadActionUrl">
	<portlet:param name="action" value="CreateResource"/>
</portlet:actionURL>
	
<div class="portlet_margin">

	<form enctype="multipart/form-data" name="inputForm" method="POST" action="<c:out value="${createResourceUploadActionUrl}"/>">
	<input type="hidden" name="eventId" value="<ww:property value="event.id"/>"/>
	
	<calendar:selectField label="labels.internal.event.assetKey" name="assetKey" multiple="false" value="assetKeys" cssClass="listBox"/>
		
	<div class="fieldrow">
		<label for="file"><ww:property value="this.getLabel('labels.internal.event.fileToAttach')"/></label><br>
		<input type="file" name="file" id="file" class="button"/>
	</div>
		
	
	<div style="height:10px"></div>
	<input type="submit" value="<ww:property value="this.getLabel('labels.internal.event.updateButton')"/>" class="button">
	<input type="button" onclick="history.back();" value="<ww:property value="this.getLabel('labels.internal.applicationCancel')"/>" class="button">
</form>

</div>

<%@ include file="adminFooter.jsp" %>
