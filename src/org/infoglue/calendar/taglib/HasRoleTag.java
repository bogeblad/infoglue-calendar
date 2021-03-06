/* ===============================================================================
*
* Part of the InfoGlue Content Management Platform (www.infoglue.org)
*
* ===============================================================================
*
*  Copyright (C)
* 
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License version 2, as published by the
* Free Software Foundation. See the file LICENSE.html for more information.
* 
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
* Place, Suite 330 / Boston, MA 02111-1307 / USA.
*
* ===============================================================================
*/
package org.infoglue.calendar.taglib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.infoglue.calendar.actions.CalendarAbstractAction;
import org.infoglue.common.util.PropertyHelper;

import com.opensymphony.webwork.ServletActionContext;


/**
 * 
 */
public class HasRoleTag extends AbstractTag 
{

	private static final long serialVersionUID = 3617579309963752240L;

	private List roles;
	private String roleName;
	
	/**
	 * 
	 */
	public HasRoleTag() 
	{
		super();
	}
	
	public int doEndTag() throws JspException
    {
		roles = (List)pageContext.getRequest().getAttribute("infoglueRemoteUserRoles");

	    boolean isFound = false;
	    Iterator i = roles.iterator();
	    while(i.hasNext())
	    {
	        String role = (String)i.next();
	        if(role.equalsIgnoreCase(roleName))
	        {
	            isFound = true;
	            break;
	        }
	    }
	    
	    setResultAttribute(new Boolean(isFound));
	    
        return EVAL_PAGE;
    }

    public void setRoleName(final String roleName) throws JspException
    {
    	CalendarAbstractAction action = new CalendarAbstractAction();
    	String translatedName = action.getSetting(roleName + "_translation");
		//String translatedName = PropertyHelper.getProperty(roleName + "_translation");
    	if(translatedName != null && translatedName.length() > 0)
    		this.roleName = translatedName;
    	else
    		this.roleName = roleName;
    }
}
