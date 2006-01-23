<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="activeNavItem" value="Events" scope="page"/>
<c:set var="activeEventSubNavItem" value="WaitingEvents" scope="page"/>

<%@ include file="adminHeader.jsp" %>

<div class="head"><ww:property value="this.getLabel('labels.internal.applicationTitle')"/><!--  - <ww:property value="this.getLabel('labels.internal.waitingEvents.subHeader')"/>--></div>

<%@ include file="functionMenu.jsp" %>

<portlet:renderURL var="createEventUrl">
	<portlet:param name="action" value="ViewCalendarList!choose"/>
</portlet:renderURL>

<%@ include file="eventSubFunctionMenu.jsp" %>

<div class="columnlabelarea">
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.event.name')"/></p></div>
	<div class="columnMedium"><p><ww:property value="this.getLabel('labels.internal.event.description')"/></p></div>
	<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.event.owningCalendar')"/></p></div>
	<div class="columnDate"><p><ww:property value="this.getLabel('labels.internal.event.startDate')"/></p></div>
	<div class="clear"></div>
</div>

<ww:iterator value="events" status="rowstatus">

	<ww:set name="eventId" value="id" scope="page"/>
	<ww:set name="name" value="name" scope="page"/>
	<portlet:renderURL var="eventUrl">
		<portlet:param name="action" value="ViewEvent"/>
		<portlet:param name="eventId" value="<%= pageContext.getAttribute("eventId").toString() %>"/>
	</portlet:renderURL>
	
	<portlet:actionURL var="deleteUrl">
		<portlet:param name="action" value="DeleteEvent"/>
		<portlet:param name="eventId" value="<%= pageContext.getAttribute("eventId").toString() %>"/>
	</portlet:actionURL>
	
	<portlet:renderURL var="viewListUrl">
		<portlet:param name="action" value="ViewWaitingEventList"/>
	</portlet:renderURL>

	<portlet:renderURL var="confirmUrl">
		<portlet:param name="action" value="Confirm"/>
		<portlet:param name="confirmTitle" value="Radera - bekr�fta"/>
		<calendar:evalParam name="confirmMessage" value="�r du s�ker p� att du vill radera &quot;${name}&quot;"/>
		<portlet:param name="okUrl" value="<%= java.net.URLEncoder.encode(pageContext.getAttribute("deleteUrl").toString(), "utf-8") %>"/>
		<portlet:param name="cancelUrl" value="<%= java.net.URLEncoder.encode(pageContext.getAttribute("viewListUrl").toString(), "utf-8") %>"/>
	</portlet:renderURL>
	
	
	<ww:if test="#rowstatus.odd == true">
    	<div class="oddrow">
    </ww:if>
    <ww:else>
		<div class="evenrow">
    </ww:else>

	   	<div class="columnMedium">
	   		<p class="portletHeadline"><a href="<c:out value="${eventUrl}"/>" title="Visa '<ww:property value="name"/>'"><ww:property value="name"/></a></p>
	   	</div>
	   	<div class="columnMedium">
	   		<p><ww:property value="shortDescription"/>&nbsp;</p>
	   	</div>
	   	<div class="columnShort">
	   		<p><ww:property value="owningCalendar.name"/></p>
	   	</div>
	   	<div class="columnDate">
	   		<p><ww:property value="this.formatDate(startDateTime.time, 'yyyy-MM-dd')"/></p>
	   	</div>
	   	<div class="columnEnd">
	   		<a href="<c:out value="${confirmUrl}"/>" title="Radera '<ww:property value="name"/>'" class="delete"></a>
	   	   	<a href="<c:out value="${eventUrl}"/>" title="Redigera '<ww:property value="name"/>'" class="edit"></a>
	   	</div>
	   	<div class="clear"></div>
	</div>

</ww:iterator>

<ww:if test="events == null || events.size() == 0">
	<div class="oddrow">
		<div class="columnLong"><p class="portletHeadline"><ww:property value="this.getLabel('labels.internal.applicationNoItemsFound')"/></a></p></div>
       	<div class="columnMedium"></div>
       	<div class="columnEnd"></div>
       	<div class="clear"></div>
    </div>
</ww:if>

<%@ include file="adminFooter.jsp" %>
