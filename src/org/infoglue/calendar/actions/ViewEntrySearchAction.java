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

import java.util.List;

import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.calendar.controllers.EventController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.controllers.ResourceController;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.Location;

import com.opensymphony.xwork.Action;

/**
 * This action represents a Location Administration screen.
 * 
 * @author Mattias Bogeblad
 */

public class ViewEntrySearchAction extends CalendarAbstractAction
{
    private Long eventId;
    private String firstName;
    private String lastName;
    private String email;
    private String[] categoryId;
    private String[] locationId;
    
    private List eventList;
    private List categoryList;
    private List locationList;
    
    private List entries;
    
    private void initialize() throws Exception
    {
        System.out.println("initialize start...");
        this.eventList = EventController.getController().getEventList();
        this.categoryList = CategoryController.getController().getCategoryList();
        this.locationList = LocationController.getController().getLocationList();
        System.out.println("initialize end...");
    }
    
    /**
     * This is the entry point for the main listing.
     */
    
    public String execute() throws Exception
    {
        initialize();

        this.entries = EntryController.getController().getEntryList(firstName, lastName, email, eventId, categoryId, locationId);
        
        return Action.SUCCESS;
    } 

    /**
     * This is the entry point for the search form.
     */
    
    public String doInput() throws Exception 
    {
        initialize();

        return Action.INPUT;
    } 
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public List getEntries()
    {
        return entries;
    }
    
    public List getCategoryList()
    {
        return categoryList;
    }
    
    public void setCategoryList(List categoryList)
    {
        this.categoryList = categoryList;
    }
    
    public List getLocationList()
    {
        return locationList;
    }
    
    public void setLocationList(List locationList)
    {
        this.locationList = locationList;
    }
    
    public String[] getCategoryId()
    {
        return categoryId;
    }
    
    public void setCategoryId(String[] categoryId)
    {
        this.categoryId = categoryId;
    }
    
    public String[] getLocationId()
    {
        return locationId;
    }
    
    public void setLocationId(String[] locationId)
    {
        this.locationId = locationId;
    }
    
    public List getEventList()
    {
        return eventList;
    }
 
    public Long getEventId()
    {
        return eventId;
    }
    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }
    
    public Object getErrorBean()
    {
        return null;
    }

}
