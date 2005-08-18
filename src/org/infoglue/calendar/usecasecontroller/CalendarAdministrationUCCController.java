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
package org.infoglue.calendar.usecasecontroller;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.infoglue.calendar.controllers.BasicController;
import org.infoglue.calendar.controllers.CalendarController;
import org.infoglue.calendar.controllers.CategoryController;
import org.infoglue.calendar.controllers.LocationController;
import org.infoglue.calendar.databeans.AdministrationUCCBean;
import org.infoglue.calendar.entities.Calendar;

/**
 * This class is responsible for collecting all infomation needed by the Calendar Admin view.
 * It does all fecthing inside one transaction which makes it very effective and fast.
 * 
 * @author mattias
 */

public class CalendarAdministrationUCCController extends BasicController
{
    public static CalendarAdministrationUCCController getController()
    {
        return new CalendarAdministrationUCCController();
    }

    /**
     * This method is used to populate all items on the AdministrationUCCBean in the view usecase.
     */
    
    public AdministrationUCCBean getDataBean(Session session) throws HibernateException, Exception 
    {
        AdministrationUCCBean administrationUCCBean = new AdministrationUCCBean();
        
        List calendars 	= CalendarController.getController().getCalendarList(session);
        List locations 	= LocationController.getController().getLocationList(session);
        List categories = CategoryController.getController().getCategoryList(session);
        administrationUCCBean.setCalendars(calendars);
        administrationUCCBean.setLocations(locations);
        administrationUCCBean.setCategories(categories);
               
        return administrationUCCBean;
    }
    
    

    
    
}
