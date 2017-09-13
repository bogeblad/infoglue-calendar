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

package org.infoglue.calendar.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Session;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.Group;
import org.infoglue.common.security.beans.InfoGluePrincipalBean;
import org.infoglue.common.settings.entities.Property;
import org.infoglue.common.util.PropertyHelper;

/**
 * This class represents the basic controller which all other controllers inherits from.
 * 
 * @author Mattias Bogeblad
 */

public abstract class BasicController
{
	private static Log log = LogFactory.getLog(BasicController.class);

	public boolean useEventPublishing(Calendar owningCalendar, Session session)
	{
		String useEventPublishing = PropertyHelper.getProperty("useEventPublishing");
		boolean result = (useEventPublishing.equalsIgnoreCase("true") ? true : false);

		if (result) {
			log.info("Use event publishing is active. Checking if calendar allows email. Calendar " + owningCalendar);
			if (owningCalendar != null) {
				try {
					String namespace = "CAL_mailDisabled";
					String key = "cal_" + owningCalendar.getId();
					Property propMailEnabled = CalendarSettingsController.getCalendarSettingsController().getProperty(namespace, key, session);
					log.info("Mail disabled property: " + propMailEnabled);
					if (propMailEnabled != null && propMailEnabled.getValue().equals("1")) {
						result = false;
					}
				} catch (Exception e) {
					log.error("Error when checking for mail disabled property. Message: " + e.getMessage());
				}
			}
		}
		return result;
	}

    public boolean useGlobalEventNotification()
    {
        String useGlobalEventNotification = PropertyHelper.getProperty("useGlobalEventNotification");
        
        return (useGlobalEventNotification.equalsIgnoreCase("true") ? true : false);
    }

    /**
     * This method checks if a user has one of the roles defined in the event.
     * @param principal
     * @param event
     * @return
     * @throws Exception
     */
    public boolean hasUserGroup(InfoGluePrincipalBean principal, Event event) throws Exception
    {
        Collection owningGroups = event.getOwningCalendar().getOwningGroups();
        if(owningGroups == null || owningGroups.size() == 0)
            return true;
        
        Iterator owningGroupsIterator = owningGroups.iterator();
        while(owningGroupsIterator.hasNext())
        {
            Group group = (Group)owningGroupsIterator.next();
            
            List principals = new ArrayList();
            principals.addAll(AccessRightController.getController().getPrincipalsWithGroup(group.getName()));
            //List principals = new ArrayList(wsh.getCollection("getPrincipals"));
            //List principals = GroupControllerProxy.getController().getInfoGluePrincipals(group.getName());

            if(principals.contains(principal))
                return true;
        }
        
        return false;
    }

}
