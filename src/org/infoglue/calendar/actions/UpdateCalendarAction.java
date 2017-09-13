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

package org.infoglue.calendar.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.CalendarSettingsController;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.validator.ValidationException;

import org.infoglue.common.settings.entities.Property;

/**
 * This action represents a Calendar Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class UpdateCalendarAction extends CalendarAbstractAction
{
	private static Log log = LogFactory.getLog(UpdateCalendarAction.class);

    private Long calendarId;
    private String name;
    private String description;
    private String[] roles;
    private String[] groups;
    private Long eventTypeId;

    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception 
    {
        try
        {
            validateInput(this);
            //log.debug("calendarId: " + calendarId);
            if(this.eventTypeId == null)
            {
                String eventTypeIdString = ServletActionContext.getRequest().getParameter("eventTypeId");

                if(eventTypeIdString != null && !eventTypeIdString.equals(""))
                	this.eventTypeId = new Long(ServletActionContext.getRequest().getParameter("eventTypeId"));
            }

            boolean mailEnabled = false;
            String mailEnabled_ = ServletActionContext.getRequest().getParameter("mailEnabled");
            mailEnabled = (mailEnabled_ != null);

            Property propMailEnabled = null;

			String namespace = "CAL_mailDisabled";
			String key = "cal_" + calendarId;
			String value = mailEnabled ? "1" : "0";

            propMailEnabled = CalendarSettingsController.getCalendarSettingsController().getProperty(namespace, key, getSession());

            if (propMailEnabled == null) {
                CalendarSettingsController.getCalendarSettingsController().createProperty(namespace, key, value, getSession());
            } else {
                 CalendarSettingsController.getCalendarSettingsController().updateProperty(namespace, key, value, getSession());
            }

            CalendarController.getController().updateCalendar(calendarId, name, description, roles, groups, eventTypeId, getSession());
        }
        catch(ValidationException e)
        {
            return Action.ERROR;
        }
        
        return Action.SUCCESS;
    } 
    
 
    public Long getCalendarId()
    {
        return calendarId;
    }
    public void setCalendarId(Long calendarId)
    {
    	this.calendarId = calendarId;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Long getEventTypeId()
    {
        return eventTypeId;
    }
    
    public void setEventTypeId(Long eventTypeId)
    {
        this.eventTypeId = eventTypeId;
    }
    
    public String[] getGroups()
    {
        return groups;
    }
    
    public void setGroups(String[] groups)
    {
        this.groups = groups;
    }
    
    public String[] getRoles()
    {
        return roles;
    }

    public void setRoles(String[] roles)
    {
        this.roles = roles;
    }
    
    public void setGroups(String groups)
    {
        this.groups = new String[] {groups};
    }
    
    public void setRoles(String roles)
    {
        this.groups = new String[] {roles};
    }

}
