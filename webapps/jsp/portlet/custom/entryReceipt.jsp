<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<ww:set name="event" value="event"/>
<ww:set name="eventVersion" value="this.getEventVersion('#event')"/>

<ww:if test="#attr.detailUrl ==''">
	Parametern DetailUrl �r ej satt. Var v�nlig �tg�rda.<br/>
</ww:if>
<ww:else>
	<ww:if test="#attr.detailUrl.indexOf('?') > -1">
		<c:set var="delim" value="&"/>
	</ww:if>
	<ww:else>
		<c:set var="delim" value="?"/>
	</ww:else>
	
	<!-- Anm&auml;lan - kvitto -->	
			  
	<H1>Kvitto - Anm&auml;lan till "<ww:property value="#eventVersion.name"/>"</H1>
	
	<div class="contaktform_receipt">
		<h3>Boknings ID:</h3>
		<p><ww:property value="entry.id"/></p>
		<h3>Namn:</h3>
		<p><ww:property value="entry.firstName"/> <ww:property value="entry.lastName"/></p>
		<h3>E-post:</h3>
		<p><ww:property value="entry.email"/></p>
		<p>En bekr&auml;ftelse p� anm&auml;lan &auml;r skickad till <ww:property value="entry.email"/></p>
		<p>V�lkommen!</p>
		<p><a href="<ww:property value="#attr.detailUrl"/><c:out value="${delim}"/>eventId=<ww:property value="eventId"/>" title="L&auml;nk till info om evenemanget">&laquo; Tillbaka till evenemangets informationssida</a></p>	
	</div>
</ww:else>

<!-- Anm&auml;lan - kvitto Slut --> 