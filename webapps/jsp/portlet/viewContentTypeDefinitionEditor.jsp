<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="calendar" prefix="calendar" %>

<portlet:defineObjects/>

<portlet:actionURL var="updateContentTypeDefinitionUrl">
	<portlet:param name="action" value="UpdateContentTypeDefinition"/>
</portlet:actionURL>

<portlet:actionURL var="newAttributeUrl">
	<portlet:param name="action" value="ViewEventType!insertAttribute"/>
</portlet:actionURL>

<portlet:actionURL var="insertAttributeValidatorUrl">
	<portlet:param name="action" value="ViewEventType!insertAttributeValidator"/>
</portlet:actionURL>

<ww:set name="contentTypeDefinitionId" value="eventTypeId" scope="page"/>
<ww:set name="eventTypeId" value="eventTypeId" scope="page"/>

<script type="text/javascript">
<!--
	function submitNewAttribute()
	{
		if(document.editForm.inputTypeId.selectedIndex > 0)
		{
			document.editForm.action = "<c:out value="${newAttributeUrl}"/>";
			document.editForm.submit();
		}
		else
		{
			alert("You must select a input type first.");
		}
	}

	function syncDropboxes()
	{
		document.editForm.inputTypeId.selectedIndex = document.editForm.inputTypeId2.selectedIndex;
	}

	function showPropertyDiv(id)
	{
		document.getElementById(id).style.visibility = 'visible';
		document.getElementById(id).style.display = 'block';
	}

	function hidePropertyDiv(id)
	{
		document.getElementById(id).style.visibility = 'hidden';
		document.getElementById(id).style.display = 'none';
	}

	function showDiv(id)
	{
		document.getElementById(id).style.visibility = 'visible';
	}

	function hideDiv(id)
	{
		document.getElementById(id).style.visibility = 'hidden';
	}

	function changeViewLanguage()
	{

		window.location.href = "ViewEventType.action?contentTypeDefinitionId=$contentTypeDefinitionId&currentContentTypeEditorViewLanguageCode=" + document.editForm.languageCode.value;
	}
	
	function checkDisplay(value, id)
	{
		if(value == "image")
		{
			document.getElementById(id).style.display = "block";
		}
		else
		{
			document.getElementById(id).style.display = "none";
		}
	}
	
	function showAddValidatorFormDiv(attributeName)
	{
		document.newValidatorForm.attributeName.value = attributeName;
		document.newValidatorForm.attributeToExpand.value = attributeName;
		showDiv('newValidatorFormLayer');
	}
	
	/****************************
	 * Hook method to get informed when a drag starts
 	 ****************************/

	function dragStarted(object)
	{
		//alert("dragStarted:" + object.id);
		isDragged = true;
	} 

	/****************************
	 * Hook method to get informed when a drag ends
	 ****************************/
	function dragEnded(object, left, top)
	{
		//alert("dragEnded:" + object.id);
	}
	
-->
</script>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/script/componentEditor.js"></script>--%>
<script type="text/javascript" src="<%=request.getContextPath()%>/script/dom-drag.js"></script>

<div id="newValidatorFormLayer" style="border: 1px solid black; background-color: white; LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
	<div id="newValidatorFormLayerPropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Add validation rule</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('newValidatorFormLayer');" class="white">close</a></div></div>
	<div id="PropertyBody" class="propertiesDivBody">
		<form name="newValidatorForm" action="<c:out value="${insertAttributeValidatorUrl}"/>" method="POST">
			<input type="hidden" name="eventTypeId" value="<ww:property value="contentTypeDefinitionId"/>">
			<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="contentTypeDefinitionId"/>">
			<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
			<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
	
			<label for="validatorName">Type:</label>
			<select size="1" name="validatorName" class="sitedropbox">
			    <option value="required">Required</option>
			    <option value="requiredif">Required If</option>
			    <option value="matchRegexp">Match Regexp</option>
			</select>
			<br/>
			<input type="submit" value="Save">
			<a href="javascript:hideDiv('newValidatorFormLayer');"><input type="button" value="Cancel"/></a>
		</form>
	</div>
</div>


<ww:iterator value="contentTypeAttributes" status="rowstatus">
	<ww:set name="attribute" value="top"/>
	<ww:set name="attributeName" value="#attribute.name" scope="page"/>
	<ww:set name="inputType" value="attribute.inputType"/>
	<ww:set name="title" value="top.getContentTypeAttribute('title').getContentTypeAttributeParameterValue().getLocalizedValue('label', currentContentTypeEditorViewLanguageCode)" scope="page"/>

	<ww:iterator value="#attribute.validators" status="rowstatus">
		<ww:set name="validator" value="top"/>
		
		<portlet:actionURL var="updateAttributeValidatorArgumentsUrl">
			<portlet:param name="action" value="ViewEventType!updateAttributeValidatorArguments"/>
		</portlet:actionURL>
		
		<div id="<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer" style="border: 1px solid black; background-color: white; LEFT:100px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
			<div id="<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>PropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Validator</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer');" class="white">close</a></div></div>
			<div id="PropertyBody" class="propertiesDivBody">
				<form name="<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>ArgumentsForm" action="<c:out value="${updateAttributeValidatorArgumentsUrl}"/>" method="POST">
					<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="eventTypeId"/>">
					<input type="hidden" name="eventTypeId" value="<ww:property value="eventTypeId"/>">
					<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
					<input type="hidden" name="attributeValidatorName" value="<ww:property value="#validator.name"/>">
					<!--
					<input type="hidden" name="attributeParameterValueLocale" value="$!currentContentTypeEditorViewLanguageCode">
					<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="$!currentContentTypeEditorViewLanguageCode">
					-->
					<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
	
					<ww:set name="index" value="0"/>
					<ww:iterator value="#validator.arguments.keySet()" status="rowstatus">
						<ww:set name="key" value="top"/>
						<input type="hidden" name="<ww:property value="#index"/>_argumentName" value="<ww:property value="#key"/>">
						
						<label for="<ww:property value="#index"/>_argumentValue"><ww:property value="#key"/>:</label>
						<input type="textfield" name="<ww:property value="#index"/>_argumentValue" value="<ww:property value="#validator.arguments.get(#key)"/>" class="normaltextfield">
				
						<ww:set name="index" value="#index + 1"/>
					</ww:iterator>
					<ww:if test="#index == 0">
						<label for="no_argumentValue">No properties for validation rule</label>
						<input type="hidden" name="no_argumentValue">
					</ww:if>
					<br/>
					<input type="submit" value="Save">
					<a href="javascript:hideDiv('<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer');"><input type="button" value="Cancel"/></a>
	
				</form>
			</div>
		</div>
	</ww:iterator>

	<ww:iterator value="#attribute.contentTypeAttributeParameters" status="rowstatus">
		<ww:set name="parameter" value="top"/>
		<ww:set name="values" value="#parameter.value.contentTypeAttributeParameterValues"/>

		<ww:iterator value="#values" status="rowstatus">
			<ww:set name="value" value="top"/>

			<ww:if test="#parameter.value.type == 1">

				<portlet:actionURL var="updateAttributeParameterValueUrl">
					<portlet:param name="action" value="ViewEventType!updateAttributeParameterValue"/>
				</portlet:actionURL>
			
				<div id="<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer" style="border: 1px solid black; background-color: white; LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">

					<div id="<ww:property value="#attribute.name"/>_<ww:property value="#parameter.key"/>_<ww:property value="#value.key"/>PropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Values</div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer');" class="white">close</a></div></div>
					<div id="PropertyBody" class="propertiesDivBody">
						<form name="<ww:property value="#attribute.name"/>_<ww:property value="#parameter.key"/>ArgumentsForm" action="<c:out value="${updateAttributeParameterValueUrl}"/>" method="POST">
							<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="eventTypeId"/>">
							<input type="hidden" name="eventTypeId" value="<ww:property value="eventTypeId"/>">
							<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
							<input type="hidden" name="attributeParameterId" value="<ww:property value="#parameter.key"/>">
							<input type="hidden" name="attributeParameterValueId" value="<ww:property value="#value.key"/>">
							<input type="hidden" name="attributeParameterValueLocale" value="<ww:property value="currentContentTypeEditorViewLanguageCode"/>">
							<!--
							<input type="hidden" name="attributeParameterValueLocale" value="$!currentContentTypeEditorViewLanguageCode">
							<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="$!currentContentTypeEditorViewLanguageCode">
							-->
							<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
			
							<label for="attributeParameterValueLabel">Label:</label>
							<input type="textfield" name="attributeParameterValueLabel" value="<ww:property value="#value.value.getLocalizedValue('label', currentContentTypeEditorViewLanguageCode)"/>" class="normaltextfield">
							<br/>
		
							<label for="newAttributeParameterValueId">Internal value:</label>
							<input type="textfield" name="newAttributeParameterValueId" value="<ww:property value="#value.value.getLocalizedValue('id', currentContentTypeEditorViewLanguageCode)"/>" class="normaltextfield">
							<br/>
							<input type="submit" value="Save">
							<a href="javascript:hideDiv('<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer');"><input type="button" value="Cancel"/></a>
			
						</form>
					</div>
				</div>

			</ww:if>
		</ww:iterator>
	</ww:iterator>


<%--
<c:if test="${activatedName == ''">
	<c:set var="attributeToExpand" value="${activatedName[0]}"/> 
</c:if>
--%>

<c:set var="visibility" value="hidden"/>
<%--
#if($attributeToExpand == $attribute.name)
	#set($visibility = "visible")
#end
#set($display = "none")
#if($attributeToExpand == $attribute.name)
	#set($display = "block")
#end
--%>

<portlet:actionURL var="updateAttributePropertyUrl">
	<portlet:param name="action" value="ViewEventType!updateAttribute"/>
</portlet:actionURL>
<div id="<ww:property value="#attribute.name"/>PropertyLayer" class="propertiesDiv" style="border: 1px solid black; background-color: white; position:absolute; left:20px; top:20px; display:<c:out value="${display}"/>; visibility:<c:out value="${visibility}"/>; z-index:0; overflow: auto;">
	<div id="<ww:property value="#attribute.name"/>PropertyHandle" class="propertiesDivHandle"><div id="propertiesDivLeftHandle" class="propertiesDivLeftHandle">Properties for attribute <ww:property value="#attribute.name"/></div><div id="propertiesDivRightHandle" class="propertiesDivRightHandle"><a href="javascript:hidePropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');" class="white">close</a></div></div>
	<div id="PropertyBody" class="propertiesDivBody">
	<form id="<ww:property value="#attribute.name"/>PropertiesForm" name="<ww:property value="#attribute.name"/>PropertiesForm" action="<c:out value="${updateAttributePropertyUrl}"/>" method="POST">
		<input type="hidden" name="eventTypeId" value="<c:out value="${eventTypeId}"/>">
		<input type="hidden" name="contentTypeDefinitionId" value="<c:out value="${contentTypeDefinitionId}"/>">
		<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="<ww:property value="currentContentTypeEditorViewLanguageCode"/>">
		<input type="hidden" name="attributeName" value="<ww:property value="#attribute.name"/>">
		<input type="hidden" name="attributeToExpand" value="<ww:property value="#attribute.name"/>">
		
	    <div class="actionrow">
			<label for="newAttributeName">Name</label>
			<input type="textfield" name="newAttributeName" value="<ww:property value="#attribute.name"/>" class="longtextfield"><br/>
	
			<calendar:selectField label="'Type'" name="'inputTypeId'" multiple="false" value="attributeTypes" selectedValue="inputType" headerItem="Choose element type" cssClass="listBox" skipContainer="true" skipLineBreak="true"/>
		</div>	
	<br/>
	
	<div class="columnlabelarea">
		<div class="columnShort"><p>Validation</p></div>
		<div class="columnLong"><p>&nbsp;</p></div>
		<div class="columnEnd"><p>&nbsp;</p></div>
		<div class="clear"></div>
	</div>
	
	<ww:set name="index" value="0"/>
	<ww:iterator value="#attribute.validators" status="rowstatus">
		<ww:set name="validator" value="top"/>
		<ww:set name="contentTypeDefinitionId" value="eventTypeId" scope="page"/>
		<ww:set name="eventTypeId" value="eventTypeId" scope="page"/>
		<ww:set name="attributeName" value="#attribute.name" scope="page"/>
		<ww:set name="validatorName" value="#validator.name" scope="page"/>
		
		<portlet:actionURL var="deleteAttributeValidatorUrl">
			<portlet:param name="action" value="ViewEventType!deleteAttributeValidator"/>
			<calendar:evalParam name="contentTypeDefinitionId" value="${contentTypeDefinitionId}"/>
			<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
			<calendar:evalParam name="attributeName" value="${attributeName}"/>
			<calendar:evalParam name="attributeValidatorName" value="${validatorName}"/>
			<calendar:evalParam name="attributeToExpand" value="${attributeName}"/>
		</portlet:actionURL>
				
		<ww:if test="#rowstatus.odd == true">
	    	<div class="oddrow">
	    </ww:if>
	    <ww:else>
			<div class="evenrow">
	    </ww:else>
		   	<div class="columnShort"><p><ww:property value="#validator.name"/></p></div>
		   	<div class="columnEnd">
				<a href="<c:out value="${deleteAttributeValidatorUrl}"/>" class="delete"></a>
				<a href="javascript:showDiv('<ww:property value="#attribute.name"/>_<ww:property value="#validator.name"/>_layer');" class="edit"></a>
			</div>
			<div class="clear"></div>
		</div
		<ww:set name="index" value="#index + 1"/>
	</ww:iterator>

	<ww:if test="#index == 0">
		<div class="actionrow">
			<div class="columnLong"><p>No validation rules assigned</p></div>
			<div class="clear"></div>
		</div>
	</ww:if>

	<div class="actionrow">
		<div class="columnLong"><p><a href="javascript:showAddValidatorFormDiv('<ww:property value="#attribute.name"/>');">Add new validation rule</a></p></div>
		<div class="clear"></div>
	</div>
	
	<br/>
	
	<div class="columnlabelarea">
		<div class="columnShort"><p>Extra parameters</p></div>
		<div class="columnLong"><p>&nbsp;</p></div>
		<div class="columnEnd"><p>&nbsp;</p></div>
		<div class="clear"></div>
	</div>
	
	<ww:iterator value="#attribute.contentTypeAttributeParameters" status="rowstatus">
		<ww:set name="parameter" value="top"/>
		<ww:set name="parameterKey" value="#parameter.key" scope="page"/>
		
	    <div class="actionrow">
	       		<input type="hidden" name="parameterNames" value="<ww:property value="#parameter.key"/>">
				
				<ww:set name="values" value="#parameter.value.contentTypeAttributeParameterValues"/>

				<ww:if test="#parameter.value.type == 0">
					<label for="<ww:property value="#parameter.key"/>"><ww:property value="#parameter.key"/>:</label>
					<input type="textfield" name="<ww:property value="#parameter.key"/>" value="<ww:property value="#parameter.value.getContentTypeAttributeParameterValue().getLocalizedValue('label', currentContentTypeEditorViewLanguageCode)"/>" class="longtextfield">
				</ww:if>
				<ww:else>
					
					<div class="columnlabelarea">
						<div class="columnShort"><p><ww:property value="#parameter.key"/></p></div>
						<div class="columnShort"><p>Label</p></div>
						<div class="columnLong"><p>Internal name</p></div>
						<div class="columnEnd"><p>&nbsp;</p></div>
						<div class="clear"></div>
					</div>
					
					<ww:iterator value="#values" status="rowstatus">
						<ww:set name="value" value="top"/>
						<ww:set name="valueId" value="#value.key" scope="page"/>
	
						<portlet:actionURL var="deleteAttributeParameterValueUrl">
							<portlet:param name="action" value="ViewEventType!deleteAttributeParameterValue"/>
							<calendar:evalParam name="contentTypeDefinitionId" value="${contentTypeDefinitionId}"/>
							<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
							<calendar:evalParam name="title" value="${title}"/>
							<calendar:evalParam name="attributeName" value="${attributeName}"/>
							<calendar:evalParam name="attributeParameterId" value="${parameterKey}"/>
							<calendar:evalParam name="attributeParameterValueId" value="${valueId}"/>
							<calendar:evalParam name="attributeToExpand" value="${attributeName}"/>
						</portlet:actionURL>
							
						<ww:if test="#rowstatus.odd == true">
					    	<div class="oddrow">
					    </ww:if>
					    <ww:else>
							<div class="evenrow">
					    </ww:else>
						   	<div class="columnShort"><p>&nbsp;</p></div>
						   	<div class="columnShort"><p><ww:property value="#value.value.getLocalizedValue('label', currentContentTypeEditorViewLanguageCode)"/></p></div>
						   	<div class="columnLong"><p><ww:property value="#value.value.getLocalizedValue('id', currentContentTypeEditorViewLanguageCode)"/></p></div>
						   	<div class="columnEnd">
								<a href="<c:out value="${deleteAttributeParameterValueUrl}"/>" class="delete"></a>
								<a href="javascript:showDiv('<ww:property value="#attribute.name"/><ww:property value="#parameter.key"/><ww:property value="#value.key"/>PropertyLayer');" class="edit"></a>
							</div>
							<div class="clear"></div>
						</div
					</ww:iterator>
					
					<portlet:actionURL var="insertAttributeParameterValueUrl">
						<portlet:param name="action" value="ViewEventType!insertAttributeParameterValue"/>
						<calendar:evalParam name="contentTypeDefinitionId" value="${contentTypeDefinitionId}"/>
						<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
						<calendar:evalParam name="title" value="${title}"/>
						<calendar:evalParam name="attributeName" value="${attributeName}"/>
						<calendar:evalParam name="attributeParameterId" value="${parameterKey}"/>
						<calendar:evalParam name="attributeToExpand" value="${attributeName}"/>
					</portlet:actionURL>
					
					<div class="actionrow">
						<div class="columnLong"><p><a href="<c:out value="${insertAttributeParameterValueUrl}"/>">Add value</a></p></div>
						<div class="clear"></div>
					</div>
				</ww:else>
		</div>
	</ww:iterator>

	<br/>
	<div class="actionrow">
		<input type="submit" value="Save"/>
		<a href="javascript:hideDiv('<ww:property value="#attribute.name"/>PropertyLayer');"><input type="button" value="Cancel"/></a>
	</div>
	</form>
	</div>
</div>

<script type="text/javascript">		
	var theHandle = document.getElementById("<ww:property value="#attribute.name"/>PropertyHandle");		
	var theRoot   = document.getElementById("<ww:property value="#attribute.name"/>PropertyLayer");		
	Drag.init(theHandle, theRoot, 50, 50, 0, 1000);    
	floatDiv("<ww:property value="#attribute.name"/>PropertyLayer", 50, 50).flt();
</script>

</ww:iterator>

#set($categoryKeys = $definedCategoryKeys)
#set($categoryList = $allCategories)

#foreach($category in $categoryKeys)
#set($categoryKey = $category.value)
<div id="${categoryKey}PropertyLayer" style="border: 1px solid black; background-color: white; LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
	<form name="${categoryKey}PropertiesForm" action="ViewEventType!updateCategoryKey.action" method="POST">
	<table border="0" cellpadding="4" cellspacing="0">
	<tr>
		<td colspan="2" class="propertiesheader">Edit Category Attributes</td>
	</tr>
	<tr>
		<td colspan="2"><img src="images/trans.gif" height="5" width="1"></td>
	</tr>
	<tr>
		<td><b>Category Key</b></td>
		<td><input type="textfield" name="newCategoryKey" value="$categoryKey" class="normaltextfield"></td>
	</tr>
	<tr>
		<td><b>Title</b></td>
		<td><input type="textfield" name="title" value="$!category.title" class="normaltextfield"></td>
	</tr>
	<tr>
		<td><b>Description</b></td>
		<td><input type="textfield" name="description" value="$!category.description" class="normaltextfield"></td>
	</tr>
	<tr>
		<td><b>Base Category</b></td>
		<td>
			#addCategorySelect("categoryId" $categoryList $category.categoryId)
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<img src="images/trans.gif" width="80" height="25" border="0">
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<input type="image" src="$ui.getString("images.managementtool.buttons.saveAndExit")" width="80" height="25" border="0"></a>
			<a href="javascript:hideDiv('${categoryKey}PropertyLayer');"><img src="$ui.getString("images.managementtool.buttons.cancel")" width="50" height="25" border="0"></a>
		</td>
	</tr>
	</table>
	<input type="hidden" name="contentTypeDefinitionId" value="$contentTypeDefinitionId">
	<input type="hidden" name="categoryKey" value="$categoryKey">
	</form>
</div>
#end


#foreach($assetKeyDefinition in $definedAssetKeys)
<div id="${assetKeyDefinition.assetKey}PropertyLayer" class="smallPropDiv" style="LEFT:250px; position:absolute; TOP:250px; visibility:hidden; z-index:1">
	<div id="${assetKey}PropertiesHandle" class="smallPropDivHandle">
		Edit AssetKey
	</div>
	<div id="${assetKeyDefinition.assetKey}PropertiesBody" class="smallPropDivBody">
		<form name="${assetKeyDefinition.assetKey}PropertiesForm" action="ViewEventType!updateAssetKey.action" method="POST">
		<p>
			<b>Asset Key:</b> <input type="textfield" name="newAssetKey" value="$assetKeyDefinition.assetKey" class="normaltextfield">
		</p>
		<p>
			<b>Max size(b):</b> <input type="textfield" name="maximumSize" value="$!assetKeyDefinition.maximumSize" class="normaltextfield">
		</p>
		<p>
			<b>AssetType:</b>
			<select name="allowedContentTypes" onchange="checkDisplay(this.value, 'imageProperties${assetKeyDefinition.assetKey}');">
			 	<option value="any" #checkSelected("$assetKeyDefinition.allowedContentTypes" "any")>Any</option>
			 	<option value="image" #checkSelected("$assetKeyDefinition.allowedContentTypes" "image")>Image</option>
			</select>
		</p>
		<div id="imageProperties${assetKeyDefinition.assetKey}" style="#if($assetKeyDefinition.allowedContentTypes == "any") display: none; #else display: block;#end">
		<p>
			<b>Width:</b>
			<input id="imageProperties${assetKeyDefinition.assetKey}width" type="textfield" name="imageWidth" value="$!assetKeyDefinition.imageWidth" class="normaltextfield">
		</p>
		<p>
			<b>Height:</b></td>
			<input id="imageProperties${assetKeyDefinition.assetKey}width" type="textfield" name="imageHeight" value="$!assetKeyDefinition.imageHeight" class="normaltextfield">
		</p>
		</div>
		<p>
			<input type="image" src="$ui.getString("images.managementtool.buttons.saveAndExit")" width="80" height="25" border="0"></a>
			<a href="javascript:hideDiv('${assetKeyDefinition.assetKey}PropertyLayer');"><img src="$ui.getString("images.managementtool.buttons.cancel")" width="50" height="25" border="0"></a>
		</p>
	</div>	
	
	<input type="hidden" name="contentTypeDefinitionId" value="$contentTypeDefinitionId">
	<input type="hidden" name="assetKey" value="${assetKeyDefinition.assetKey}">
	</form>
</div>
#end


<form name="editForm" method="POST" action="<c:out value="${updateContentTypeDefinitionUrl}"/>">
<input type="hidden" name="eventTypeId" value="<ww:property value="contentTypeDefinitionId"/>">
<input type="hidden" name="contentTypeDefinitionId" value="<ww:property value="contentTypeDefinitionId"/>">
<input type="hidden" name="currentContentTypeEditorViewLanguageCode" value="<ww:property value="${currentContentTypeEditorViewLanguageCode}"/>">
<input type="hidden" name="schemaValue" value="$formatter.escapeHTML($!schemaValue)">
<div id="menu">

<div class="columnlabelarea">
	<div class="columnShort"><p><ww:property value="this.getLabel('labels.internal.eventType.attributes')"/></p></div>
	<div class="columnLong">
		<select size="1" name="inputTypeId" class="sitedropbox">
		    <option value="" selected>Choose element type</option>
		    <!--<option value="label">Label</option>-->
		    <option value="textfield">TextField</option>
		    <option value="textarea">TextArea</option>
		    <option value="checkbox">CheckBox</option>
		    <option value="radiobutton">RadioButton</option>
		    <option value="select">SelectBox</option>
		    <option value="hidden">Hidden</option>
		    <!--<option value="password">Password</option>-->
		    <!--<option value="image">SubmitImage</option>-->
		    <!--<option value="submit">SubmitButton</option>-->
		    <!--<option value="clear">ClearButton</option>-->
		</select>
		<a href="javascript:submitNewAttribute();"><input type="button" value="Add attribute" style=""/></a>
	</div>
	<div class="columnShort">
		<p>
			<a href="javascript:showDiv('simpleEditor');"><ww:property value="this.getLabel('labels.internal.eventType.simple')"/></a>
		</p>
	</div>
	<div class="columnEnd">
		<select onChange="javascript:changeViewLanguage();" size="1" name="languageCode" class="sitedropbox">
		    <option value="">default</option>
			#foreach ($languageVO in $availableLanguages)
				#if($languageVO.languageCode == $!currentContentTypeEditorViewLanguageCode)
				<option value="$languageVO.getLanguageCode()" selected>$languageVO.getName()</option>
				#else
				<option value="$languageVO.getLanguageCode()">$languageVO.getName()</option>
				#end
			#end
		</select>
	
	</div>
	<div class="clear"></div>
</div>

<div id="attributes">
	<c:set var="count" value="0"/>
	<ww:iterator value="contentTypeAttributes" status="rowstatus">
		<ww:set name="attribute" value="top" scope="page"/>
		<ww:set name="attribute" value="top"/>
		<ww:set name="title" value="top.getContentTypeAttribute('title').getContentTypeAttributeParameterValue().getLocalizedValue('label', '$!currentContentTypeEditorViewLanguageCode')" scope="page"/>
		
		<portlet:actionURL var="deleteAttributeUrl">
			<portlet:param name="action" value="ViewEventType!deleteAttribute"/>
			<calendar:evalParam name="eventTypeId" value="${contentTypeDefinitionId}"/>
			<calendar:evalParam name="title" value="${title}"/>
			<calendar:evalParam name="attributeName" value="${attribute.name}"/>
		</portlet:actionURL>
		
		<ww:if test="#rowstatus.odd == true">
	    	<div class="oddrow">
	    </ww:if>
	    <ww:else>
			<div class="evenrow">
	    </ww:else>

		   	<div class="columnShort">
				<a href="ViewEventType!moveAttributeUp.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&attributeName=$attribute.name" class="moveup"></a>
				<a href="ViewEventType!moveAttributeDown.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&attributeName=$attribute.name" class="moveDown"></a>
				<a href="#" title="<c:out value="${attribute.inputType}"/>" class="<c:out value="${attribute.inputType}"/>Icon"></a>
			</div
			<div class="columnLong">
				<a name="<ww:property value="#attribute.name"/>" href="javascript:showPropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');">
				<ww:property value="#attribute.name"/> (<c:out value="${title}"/>) of type <c:out value="${attribute.inputType}"/></a>
			</div>
			<div class="columnEnd">
				<a href="<c:out value="${deleteAttributeUrl}"/>" class="delete"></a>
				<a href="javascript:showPropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');" class="edit"></a>
			</div>
			<div class="clear"></div>
		</div>
		<ww:set name="count" value="${count + 1)"/>
	</ww:iterator>
</div>
<div id="menu">
	<ww:if test="count > 15">

		<select size="1" name="inputTypeId2" onChange="syncDropboxes();" class="sitedropbox">
		    <option value="" selected>Choose element type</option>
		    <!--<option value="label">Label</option>-->
		    <option value="textfield">TextField</option>
		    <option value="textarea">TextArea</option>
		    <option value="checkbox">CheckBox</option>
		    <option value="radiobutton">RadioButton</option>
		    <option value="select">SelectBox</option>
		    <option value="hidden">Hidden</option>
		    <!--<option value="password">Password</option>-->
		    <!--<option value="image">SubmitImage</option>-->
		    <!--<option value="submit">SubmitButton</option>-->
		    <!--<option value="clear">ClearButton</option>-->
		</select>

		<a href="javascript:submitNewAttribute();"><input type="button" value="Add attribute" style=""/></a>

	</ww:if>
</div>

<%--
<div class="columnlabelarea">
	<div class="columnLong">Defined Categories</div>
	<div class="columnEnd">&nbsp;</div>
	<div class="clear"></div>
</div>

<div class="columnlabelarea">
	<div class="columnMedium">Attribute (key)</div>
	<div class="columnMedium">Base Category</div>
	<div class="columnEnd">&nbsp;</div>
	<div class="clear"></div>
</div>

<div id="categories">

	<c:set var="count" value="0"/>
	<ww:iterator value="categoryKeys" status="rowstatus">
		<ww:set name="category" value="top" scope="page"/>
		<ww:set name="categoryKey" value="top.value" scope="page"/>
		
		<ww:if test="#rowstatus.odd == true">
	    	<div class="oddrow">
	    </ww:if>
	    <ww:else>
			<div class="evenrow">
	    </ww:else>

		   	<div class="columnShort">
				<a href="ViewEventType!moveAttributeUp.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&attributeName=$attribute.name" class="moveup"></a>
				<a href="ViewEventType!moveAttributeDown.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&attributeName=$attribute.name" class="moveDown"></a>
				<a href="#" title="<c:out value="${attribute.inputType}"/>" class="<c:out value="${attribute.inputType}"/>Icon"></a>
			</div
			<div class="columnLong">
				<a name="<ww:property value="#attribute.name"/>" href="javascript:showPropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');">
				<ww:property value="#attribute.name"/> (<c:out value="${title}"/>) of type <c:out value="${attribute.inputType}"/></a>
			</div>
			<div class="columnEnd">
				<a href="ViewEventType!deleteAttribute.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&attributeName=$attribute.name" class="delete"></a>
				<a href="javascript:showPropertyDiv('<ww:property value="#attribute.name"/>PropertyLayer');" class="edit"></a>
			</div>
			<div class="clear"></div>
		</div>
		<ww:set name="count" value="${count + 1)"/>
	</ww:iterator>
</div>

<table class="managementtooledit" cellpadding="2" cellspacing="2" border="0" width="100%">
<tr>
	<td>
		<table width="700" cellpadding="0" cellspacing="2" border="0">
			<tr>
				<td align="left"><b>Defined Categories</b></td>
				<td align="right">
					<a href="ViewEventType!insertCategoryKey.action?contentTypeDefinitionId=$contentTypeDefinitionId">Add Category</a>
				</td>
			</tr>
		</table>
		<table width="700" cellpadding="0" cellspacing="2" border="0" class="bordered">
			<tr>
				<td width="45%"><b>Attribute (Key)</b></td>
				<td width="45%"><b>Base Category</b></td>
				<td/>
			</tr>
		#foreach($category in $categoryKeys)
			#set($categoryKey = $category.value)
			<tr>
				<td width="45%">$category.title ($categoryKey)</td>
				<td width="45%">$category.categoryName</td>
				<td align="right">
					<nobr>
					<a href="javascript:showDiv('${categoryKey}PropertyLayer');"><img src="images/properties.gif" border="0"></a>
					<a href="ViewEventType!deleteCategoryKey.action?contentTypeDefinitionId=$contentTypeDefinitionId&categoryKey=$categoryKey"><img src="images/delete.gif" border="0"></a>
					</nobr>
				</td>
			</tr>
		#end
		</table>
	</td>
</tr>
</table>

<table class="managementtooledit" cellpadding="2" cellspacing="2" border="0" width="100%">
<tr>
	<td>
		<table width="700" cellpadding="0" cellspacing="2" border="0">
			<tr>
				<td align="left"><b>Defined Asset Keys</b></td>
				<td align="right">
					<a href="ViewEventType!insertAssetKey.action?contentTypeDefinitionId=$contentTypeDefinitionId">Add Asset Key</a>
				</td>
			</tr>
		</table>
		<table width="700" cellpadding="0" cellspacing="2" border="0" class="bordered">
		#foreach($assetKeyDefinition in $definedAssetKeys)
			<tr>
				<td><nobr><a href="ViewEventType!moveAssetKeyUp.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&assetKey=$assetKeyDefinition.assetKey"><img src="images/moveUp.gif" border="0"></a><a href="ViewEventType!moveAssetKeyDown.action?contentTypeDefinitionId=$contentTypeDefinitionId&title=$title&assetKey=$assetKeyDefinition.assetKey"><img src="images/moveDown.gif" border="0"></a></nobr></td>
				<td width="90%">$assetKeyDefinition.assetKey</td>
				<td align="right">
					<nobr>
					<a href="javascript:showDiv('${assetKeyDefinition.assetKey}PropertyLayer');"><img src="images/properties.gif" border="0"></a>
					<a href="ViewEventType!deleteAssetKey.action?contentTypeDefinitionId=$contentTypeDefinitionId&assetKey=$assetKeyDefinition.assetKey"><img src="images/delete.gif" border="0"></a>
					</nobr>
				</td>
			</tr>
		#end
		</table>
	</td>
</tr>
<tr>
	<td>&nbsp;</th>
</tr>
<tr>
	<td>
		<input type="image" src="$ui.getString("images.managementtool.buttons.save")" width="50" height="25">
		<a href="javascript:saveAndExit(document.editForm, 'UpdateContentTypeDefinition!saveAndExit.action');"><img src="$ui.getString("images.managementtool.buttons.saveAndExit")" width="80" height="25" border="0"></a>
		<a href="ViewListContentTypeDefinition.action"><img src="$ui.getString("images.managementtool.buttons.cancel")" width="50" height="25" border="0"></a>
	</td>
</tr>
</table>
--%>
</form>
</div>

<!-- HERE IS THE SIMPLE EDITOR DIV -->
<div id="simpleEditor" style="visibility: hidden; position: absolute; top: 200px; left: 20px; width: 90%;">
<form>
<textarea name="schemaValue" style="width: 100%; height: 500px;"><ww:property value="contentTypeDefinition.schemaValue"/></textarea>
<input type="submit" value="Save"/>
</form>
</div>

<script type="text/javascript">
	#if($activatedName.size() > 0)
		document.location.href = document.location.href + "#$activatedName.get(0)"; 
		showDiv("${activatedName.get(0)}PropertyLayer");
	#end
</script>

