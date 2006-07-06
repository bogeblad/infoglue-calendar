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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.calendar.entities.Calendar;
import org.infoglue.calendar.entities.Category;
import org.infoglue.calendar.entities.Event;
import org.infoglue.calendar.entities.EventCategory;
import org.infoglue.calendar.entities.EventTypeCategoryAttribute;
import org.infoglue.calendar.entities.Group;
import org.infoglue.calendar.entities.Location;
import org.infoglue.calendar.entities.Participant;
import org.infoglue.calendar.entities.Role;
import org.infoglue.calendar.entities.Subscriber;
import org.infoglue.calendar.util.EventComparator;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.security.InfoGlueRole;
import org.infoglue.cms.util.sorters.SiteNodeComparator;
import org.infoglue.cms.controllers.kernel.impl.simple.RoleControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.GroupControllerProxy;
import org.infoglue.cms.controllers.kernel.impl.simple.UserControllerProxy;
import org.infoglue.common.util.PropertyHelper;
import org.infoglue.common.util.RemoteCacheUpdater;
import org.infoglue.common.util.VelocityTemplateProcessor;
import org.infoglue.common.util.io.FileHelper;
import org.infoglue.common.util.mail.MailServiceFactory;


import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.NotExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class EventController extends BasicController
{    
    //Logger for this class
    private static Log log = LogFactory.getLog(EventController.class);
        
    
    /**
     * Factory method to get EventController
     * 
     * @return EventController
     */
    
    public static EventController getController()
    {
        return new EventController();
    }
        
    
    /**
     * This method is used to create a new Event object in the database.
     */
    
    public Event createEvent(Long calendarId, 
            				String name, 
            				String description, 
            				Boolean isInternal, 
            	            Boolean isOrganizedByGU, 
            	            String organizerName, 
            	            String lecturer, 
            	            String customLocation,
            	            String alternativeLocation,
            	            String shortDescription,
            	            String longDescription,
            	            String eventUrl,
            	            String contactName,
            	            String contactEmail,
            	            String contactPhone,
            	            String price,
            	            java.util.Calendar lastRegistrationCalendar,
            	            Integer maximumParticipants,
            	            java.util.Calendar startDateTime, 
            	            java.util.Calendar endDateTime, 
            	            Set oldLocations, 
            	            Set oldEventCategories, 
            	            Set oldParticipants,
            	            Integer stateId,
            	            String creator,
            	            Long entryFormId,
            	            String xml,
            	            Session session) throws HibernateException, Exception 
    {
        Event event = null;
 
		Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
		
		Set locations = new HashSet();
		Iterator oldLocationsIterator = oldLocations.iterator();
		while(oldLocationsIterator.hasNext())
		{
		    Location location = (Location)oldLocationsIterator.next();
		    locations.add(location);
		}
		
		Set participants = new HashSet();
		Iterator oldParticipantsIterator = oldParticipants.iterator();
		while(oldParticipantsIterator.hasNext())
		{
		    Participant oldParticipant = (Participant)oldParticipantsIterator.next();
		    Participant participant = new Participant();
		    participant.setUserName(oldParticipant.getUserName());
		    participant.setEvent(event);
		    session.save(participant);
		    participants.add(participant);
		}
		
		event = createEvent(calendar, 
		        			name, 
		        			description, 
		        			isInternal, 
		                    isOrganizedByGU, 
		                    organizerName, 
		                    lecturer, 
		                    customLocation,
		                    alternativeLocation,
		                    shortDescription,
		                    longDescription,
		                    eventUrl,
		                    contactName,
		                    contactEmail,
		                    contactPhone,
		                    price,
		                    lastRegistrationCalendar,
		                    maximumParticipants,
		        			startDateTime, 
		        			endDateTime, 
		        			locations, 
		        			participants,
		        			stateId,
		        			creator,
		        			entryFormId,
		        			xml,
		        			session);
		
		Set eventCategories = new HashSet();
		Iterator oldEventCategoriesIterator = oldEventCategories.iterator();
		while(oldEventCategoriesIterator.hasNext())
		{
		    EventCategory oldEventCategory = (EventCategory)oldEventCategoriesIterator.next();
		    
		    EventCategory eventCategory = new EventCategory();
		    eventCategory.setEvent(event);
		    eventCategory.setCategory(oldEventCategory.getCategory());
		    eventCategory.setEventTypeCategoryAttribute(oldEventCategory.getEventTypeCategoryAttribute());
		    session.save(eventCategory);
		    
	        eventCategories.add(eventCategory);
	    
		}

		event.setEventCategories(eventCategories);
		
        return event;
    }

    
    
    /**
     * This method is used to create a new Event object in the database.
     */
    
    public Event createEvent(Long calendarId, 
            				String name, 
            				String description, 
            				Boolean isInternal, 
            	            Boolean isOrganizedByGU, 
            	            String organizerName, 
            	            String lecturer, 
            	            String customLocation,
            	            String alternativeLocation,
            	            String shortDescription,
            	            String longDescription,
            	            String eventUrl,
            	            String contactName,
            	            String contactEmail,
            	            String contactPhone,
            	            String price,
            	            java.util.Calendar lastRegistrationCalendar,
            	            Integer maximumParticipants,
            	            java.util.Calendar startDateTime, 
            	            java.util.Calendar endDateTime, 
            	            String[] locationId, 
            	            Map categoryAttributes, 
            	            String[] participantUserName,
            	            Integer stateId,
            	            String creator,
            	            Long entryFormId,
            	            String xml,
            	            Session session) throws HibernateException, Exception 
    {
        Event event = null;
 
		Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
		
		Set locations = new HashSet();
		if(locationId != null)
		{
			for(int i=0; i<locationId.length; i++)
			{
			    if(!locationId[i].equals(""))
			    {
				    Location location = LocationController.getController().getLocation(new Long(locationId[i]), session);
				    locations.add(location);
			    }
			}
		}
		
		Set participants = new HashSet();
		if(participantUserName != null)
		{
			for(int i=0; i<participantUserName.length; i++)
			{
			    Participant participant = new Participant();
			    participant.setUserName(participantUserName[i]);
			    participant.setEvent(event);
			    session.save(participant);
			    participants.add(participant);
			}
		}
		
		event = createEvent(calendar, 
		        			name, 
		        			description, 
		        			isInternal, 
		                    isOrganizedByGU, 
		                    organizerName, 
		                    lecturer, 
		                    customLocation,
		                    alternativeLocation,
		                    shortDescription,
		                    longDescription,
		                    eventUrl,
		                    contactName,
		                    contactEmail,
		                    contactPhone,
		                    price,
		                    lastRegistrationCalendar,
		                    maximumParticipants,
		        			startDateTime, 
		        			endDateTime, 
		        			locations, 
		        			participants,
		        			stateId,
		        			creator,
		        			entryFormId,
		        			xml,
		        			session);
		
		Set eventCategories = new HashSet();
		if(categoryAttributes != null)
		{
			Iterator categoryAttributesIterator = categoryAttributes.keySet().iterator();
			while(categoryAttributesIterator.hasNext())
			{
			    String categoryAttributeId = (String)categoryAttributesIterator.next(); 
			    log.info("categoryAttributeId:" + categoryAttributeId);
			    EventTypeCategoryAttribute eventTypeCategoryAttribute = EventTypeCategoryAttributeController.getController().getEventTypeCategoryAttribute(new Long(categoryAttributeId), session);
			     
			    String[] categoriesArray = (String[])categoryAttributes.get(categoryAttributeId);
			    for(int i=0; i < categoriesArray.length; i++)
			    {
			        Category category = CategoryController.getController().getCategory(new Long(categoriesArray[i]), session);
			        
			        EventCategory eventCategory = new EventCategory();
				    eventCategory.setEvent(event);
				    eventCategory.setCategory(category);
				    eventCategory.setEventTypeCategoryAttribute(eventTypeCategoryAttribute);
				    session.save(eventCategory);
				    
			        eventCategories.add(eventCategory);
			    }
			}
		}
		event.setEventCategories(eventCategories);
		
        return event;
    }

    
    /**
     * This method is used to create a new Event object in the database inside a transaction.
     */
    
    public Event createEvent(Calendar owningCalendar, 
            				String name, 
            				String description, 
            				Boolean isInternal, 
            	            Boolean isOrganizedByGU, 
            	            String organizerName, 
            	            String lecturer, 
            	            String customLocation,
            	            String alternativeLocation,
            	            String shortDescription,
            	            String longDescription,
            	            String eventUrl,
            	            String contactName,
            	            String contactEmail,
            	            String contactPhone,
            	            String price,
            	            java.util.Calendar lastRegistrationCalendar,
            	            Integer maximumParticipants,
            	            java.util.Calendar startDateTime, 
            				java.util.Calendar endDateTime, 
            				Set locations, 
            				Set participants,
            				Integer stateId,
            				String creator,
            				Long entryFormId,
            				String xml,
            				Session session) throws HibernateException, Exception 
    {
        
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setIsInternal(isInternal);
        event.setIsOrganizedByGU(isOrganizedByGU);
        event.setOrganizerName(organizerName);
        event.setLecturer(lecturer);
        event.setCustomLocation(customLocation);
        event.setAlternativeLocation(alternativeLocation);
        event.setShortDescription(shortDescription);
        event.setLongDescription(longDescription);
        event.setEventUrl(eventUrl);
        event.setContactName(contactName);
        event.setContactEmail(contactEmail);
        event.setContactPhone(contactPhone);
        event.setPrice(price);
        event.setMaximumParticipants(maximumParticipants);
        event.setLastRegistrationDateTime(lastRegistrationCalendar);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime); 
        event.setStateId(stateId);
        event.setCreator(creator);
        event.setEntryFormId(entryFormId);
        event.setAttributes(xml);
        
        event.setOwningCalendar(owningCalendar);
        event.getCalendars().add(owningCalendar);
        event.setLocations(locations);
        event.setParticipants(participants);
        owningCalendar.getEvents().add(event);
        
        session.save(event);
        
        return event;
    }
    
    
    /**
     * Updates an event.
     * 
     * @throws Exception
     */
    
    public void updateEvent(
            Long id, 
            String name, 
            String description, 
            Boolean isInternal, 
            Boolean isOrganizedByGU, 
            String organizerName, 
            String lecturer, 
            String customLocation,
            String alternativeLocation,
            String shortDescription,
            String longDescription,
            String eventUrl,
            String contactName,
            String contactEmail,
            String contactPhone,
            String price,
            java.util.Calendar lastRegistrationCalendar,
            Integer maximumParticipants,
            java.util.Calendar startDateTime, 
            java.util.Calendar endDateTime, 
            String[] locationId, 
            Map categoryAttributes, 
            String[] participantUserName,
            Long entryFormId,
            String xml,
            Session session) throws Exception 
    {

        Event event = getEvent(id, session);
		
		Set locations = new HashSet();
		if(locationId != null)
		{
			for(int i=0; i<locationId.length; i++)
			{
			    if(!locationId[i].equalsIgnoreCase(""))
			    {
			        Location location = LocationController.getController().getLocation(new Long(locationId[i]), session);
			        locations.add(location);
			    }
			}
		}
		
	    log.info("participantUserName: " + participantUserName);
		Set participants = new HashSet();
		if(participantUserName != null)
		{
			for(int i=0; i<participantUserName.length; i++)
			{
			    Participant participant = new Participant();
			    participant.setUserName(participantUserName[i]);
			    participant.setEvent(event);
			    log.info("Adding " + participantUserName[i]);

			    session.save(participant);
			    participants.add(participant);
			}
		}
		
		updateEvent(
		        event, 
		        name, 
		        description, 
		        isInternal, 
		        isOrganizedByGU, 
		        organizerName, 
		        lecturer, 
		        customLocation,
		        alternativeLocation,
                shortDescription,
                longDescription,
                eventUrl,
                contactName,
                contactEmail,
                contactPhone,
                price,
                lastRegistrationCalendar,
                maximumParticipants,
		        startDateTime, 
		        endDateTime, 
		        locations, 
		        categoryAttributes, 
		        participants, 
		        entryFormId,
		        xml,
		        session);
		
    }
    
    /**
     * Updates an event inside an transaction.
     * 
     * @throws Exception
     */
    
    public void updateEvent(
            Event event, 
            String name, 
            String description, 
            Boolean isInternal, 
            Boolean isOrganizedByGU, 
            String organizerName, 
            String lecturer, 
            String customLocation,
            String alternativeLocation,
            String shortDescription,
            String longDescription,
            String eventUrl,
            String contactName,
            String contactEmail,
            String contactPhone,
            String price,
            java.util.Calendar lastRegistrationCalendar,
            Integer maximumParticipants,
            java.util.Calendar startDateTime, 
            java.util.Calendar endDateTime, 
            Set locations, 
            Map categoryAttributes, 
            Set participants, 
            Long entryFormId,
            String xml,
            Session session) throws Exception 
    {
        event.setName(name);
        event.setDescription(description);
        event.setIsInternal(isInternal);
        event.setIsOrganizedByGU(isOrganizedByGU);
        event.setOrganizerName(organizerName);
        event.setLecturer(lecturer);
        event.setCustomLocation(customLocation);
        event.setAlternativeLocation(alternativeLocation);
        event.setShortDescription(shortDescription);
        event.setLongDescription(longDescription);
        event.setEventUrl(eventUrl);
        event.setContactName(contactName);
        event.setContactEmail(contactEmail);
        event.setContactPhone(contactPhone);
        event.setPrice(price);
        event.setMaximumParticipants(maximumParticipants);
        event.setLastRegistrationDateTime(lastRegistrationCalendar);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime);
        event.setLocations(locations);
        event.setEntryFormId(entryFormId);
        event.setAttributes(xml);
        
        Iterator eventCategoryIterator = event.getEventCategories().iterator();
		while(eventCategoryIterator.hasNext())
		{
		    EventCategory eventCategory = (EventCategory)eventCategoryIterator.next();
		    session.delete(eventCategory);
		}
		
        Set eventCategories = new HashSet();
		if(categoryAttributes != null)
		{
			Iterator categoryAttributesIterator = categoryAttributes.keySet().iterator();
			while(categoryAttributesIterator.hasNext())
			{
			    String categoryAttributeId = (String)categoryAttributesIterator.next(); 
			    log.info("categoryAttributeId:" + categoryAttributeId);
			    EventTypeCategoryAttribute eventTypeCategoryAttribute = EventTypeCategoryAttributeController.getController().getEventTypeCategoryAttribute(new Long(categoryAttributeId), session);
			     
			    String[] categoriesArray = (String[])categoryAttributes.get(categoryAttributeId);
			    for(int i=0; i < categoriesArray.length; i++)
			    {
			        Category category = CategoryController.getController().getCategory(new Long(categoriesArray[i]), session);
			        
			        EventCategory eventCategory = new EventCategory();
				    eventCategory.setEvent(event);
				    eventCategory.setCategory(category);
				    eventCategory.setEventTypeCategoryAttribute(eventTypeCategoryAttribute);
				    session.save(eventCategory);
				    
			        eventCategories.add(eventCategory);
			    }
			}
		}
		event.setEventCategories(eventCategories);
        
        event.setParticipants(participants);
        
		session.update(event);
		
		if(event.getStateId().equals(Event.STATE_PUBLISHED))
		    new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());
	}
    

    /**
     * This method is used to create a new Event object in the database.
     */
    
    public void linkEvent(Long calendarId, Long eventId, Session session) throws HibernateException, Exception 
    {
        Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
        Event event = EventController.getController().getEvent(eventId, session);		

        event.getCalendars().add(calendar);
        
		new RemoteCacheUpdater().updateRemoteCaches(calendarId);
    }

    /**
     * Submits an event for publication.
     * 
     * @throws Exception
     */
    
    public void submitForPublishEvent(Long id, String publishEventUrl, Session session) throws Exception 
    {
		Event event = getEvent(id, session);
		event.setStateId(Event.STATE_PUBLISH);
		
        if(useEventPublishing())
        {
            try
            {
                EventController.getController().notifyPublisher(event, publishEventUrl);
            }
            catch(Exception e)
            {
                log.warn("An error occcurred:" + e.getMessage(), e);
            }
        }

    }    

    
    /**
     * Publishes an event.
     * 
     * @throws Exception
     */
    
    public void publishEvent(Long id, String publishedEventUrl, Session session) throws Exception 
    {
		Event event = getEvent(id, session);
		event.setStateId(Event.STATE_PUBLISHED);
		
		new RemoteCacheUpdater().updateRemoteCaches(event.getOwningCalendar().getId());
		
        if(useGlobalEventNotification())
        {
            try
            {
                EventController.getController().notifySubscribers(event, publishedEventUrl);
            }
            catch(Exception e)
            {
                log.warn("An error occcurred:" + e.getMessage(), e);
            }
        }

    }    
    
    /**
     * This method returns a Event based on it's primary key inside a transaction
     * @return Event
     * @throws Exception
     */
    
    public Event getEvent(Long id, Session session) throws Exception
    {
        Event event = (Event)session.load(Event.class, id);
		
		return event;
    }
    
    
    
    /**
     * Gets a list of all events available for a particular day.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEventList(Session session) throws Exception 
    {
        List result = null;
        
        Query q = session.createQuery("from Event event order by event.id");
   
        result = q.list();
        
        return result;
    }
    
    
    /**
     * Gets a list of all events matching the arguments given.
     * @return List of Event
     * @throws Exception
     */
    
    public List getExpiredEventList(java.util.Calendar now/*, java.util.Calendar lastCheckedDate*/, Session session) throws Exception 
    {
        java.util.Calendar recentExpirations = java.util.Calendar.getInstance();
        recentExpirations.setTime(now.getTime());
        recentExpirations.add(java.util.Calendar.HOUR_OF_DAY, -1);
        
        List result = null;
        log.info("Checking for any events which are published and which have expired just now..");
        
        Criteria criteria = session.createCriteria(Event.class);
        criteria.add(Expression.lt("endDateTime", now));
        criteria.add(Expression.gt("endDateTime", recentExpirations));

        //criteria.add(Expression.gt("endDateTime", lastCheckedDate));

        log.info("endDateTime:" + now.getTime());
        
        result = criteria.list();
        
        return result;
    }

    
    /**
     * Gets a list of all events matching the arguments given.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEventList(String name,
            java.util.Calendar startDateTime,
            java.util.Calendar endDateTime,
        	String organizerName,
        	String lecturer,
            String customLocation,
            String alternativeLocation,
            String contactName,
            String contactEmail,
            String contactPhone,
            String price,
            Integer maximumParticipants,
            Boolean sortAscending,
            Session session) throws Exception 
    {
        List result = null;
        
        List arguments = new ArrayList();
        List values = new ArrayList();
        
        if(name != null && name.length() > 0)
        {
            arguments.add("event.name like ?");
            values.add("%" + name + "%");
        }
        if(organizerName != null && organizerName.length() > 0)
        {
            arguments.add("event.organizerName like ?");
            values.add("%" + organizerName + "%");
        }
        if(lecturer != null && lecturer.length() > 0)
        {
            arguments.add("event.lecturer like ?");
            values.add("%" + lecturer + "%");
        }
        if(customLocation != null && customLocation.length() > 0)
        {
            arguments.add("event.customLocation like ?");
            values.add("%" + customLocation + "%");
        }
        if(alternativeLocation != null && alternativeLocation.length() > 0)
        {
            arguments.add("event.alternativeLocation like ?");
            values.add("%" + alternativeLocation + "%");
        }
        if(contactName != null && contactName.length() > 0)
        {
            arguments.add("event.contactName like ?");
            values.add("%" + contactName + "%");
        }
        if(contactEmail != null && contactEmail.length() > 0)
        {
            arguments.add("event.contactEmail like ?");
            values.add("%" + contactEmail + "%");
        }
        if(contactPhone != null && contactPhone.length() > 0)
        {
            arguments.add("event.contactPhone like ?");
            values.add("%" + contactPhone + "%");
        }
        if(price != null && price.length() > 0)
        {
            arguments.add("event.price = ?");
            values.add(price);
        }
        if(maximumParticipants != null)
        {						 
            arguments.add("event.maximumParticipants = ?");
            values.add(maximumParticipants);
        }
        if(startDateTime != null)
        {						 
            arguments.add("event.startDateTime >= ?");
            values.add(startDateTime);
        }
        if(endDateTime != null)
        {						 
            arguments.add("event.endDateTime <= ?");
            values.add(endDateTime);
        }

        String argumentsSQL = "";
        Iterator argumentsIterator = arguments.iterator();
        while(argumentsIterator.hasNext())
        {
            if(argumentsSQL.length() > 0)
                argumentsSQL += " AND ";
            argumentsSQL += (String)argumentsIterator.next();
        }
        log.info("argumentsSQL:" + argumentsSQL);
        
        String order = "desc";
        if(sortAscending.booleanValue())
            order = "asc";
        
        Query q = session.createQuery("from Event event " + (argumentsSQL.length() > 0 ? "WHERE " + argumentsSQL : "") + " order by event.startDateTime " + order);
   
        int i = 0;
        Iterator valuesIterator = values.iterator();
        while(valuesIterator.hasNext())
        {
            Object o = valuesIterator.next();
            if(o instanceof Float)
                q.setFloat(i, ((Float)o).floatValue());
            else if(o instanceof Integer)
                q.setInteger(i, ((Integer)o).intValue());
            else if(o instanceof String)
                q.setString(i, (String)o);
            else if(o instanceof java.util.Calendar)
                q.setCalendar(i, (java.util.Calendar)o);
            
            i++;
        }
        
        result = q.list();
        
        return result;
    }
    
    
    
    /**
     * Gets a list of all events available for a particular user.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getEventList(java.util.Calendar startDate, java.util.Calendar endDate, String userName, List roles, List groups, Integer stateId, boolean includeLinked, boolean includeEventsCreatedByUser, Session session) throws Exception 
    {
        List result = new ArrayList();
        
        if(includeLinked == true)
        {
            String rolesSQL = getRoleSQL(roles);
            log.info("groups:" + groups.size());
	        String groupsSQL = getGroupsSQL(groups);
	        log.info("groupsSQL:" + groupsSQL);
	        String sql = "select distinct c from Calendar c, Role cr, Group g where cr.calendar = c AND g.calendar = c " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by c.id";
	        //String sql = "select distinct event from Event event, Calendar c, Event_Calendar ec, Role cr, Group g where event.calendar = c AND cr.calendar = c AND g.calendar = c AND event.stateId = ? " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by event.id";
	        log.info("sql:" + sql);
	        Query q = session.createQuery(sql);
	        setRoleNames(0, q, roles);
	        setGroupNames(roles.size(), q, groups);
	        List calendars = q.list();

	        Object[] calendarIdArray = new Object[calendars.size()];

	        int i = 0;
	        Iterator calendarsIterator = calendars.iterator();
	        while(calendarsIterator.hasNext())
	        {
	            Calendar calendar = (Calendar)calendarsIterator.next();
	            log.info("calendar: " + calendar.getName());
	            calendarIdArray[i] = calendar.getId();
	            i++;                
	        }
	        
            if(calendarIdArray.length > 0)
            {
	            Criteria criteria = session.createCriteria(Event.class);
	            criteria.add(Restrictions.eq("stateId", stateId));
	            criteria.add(Expression.gt("endDateTime", startDate));
	            criteria.addOrder(Order.asc("startDateTime"));
	            
	            /*
	            criteria.add(Expression.gt("endDateTime", endDate));
	            criteria.add(Expression.lt("startDateTime", startDate));
	            */
	            criteria.createCriteria("owningCalendar")
	            .add(Restrictions.not(Restrictions.in("id", calendarIdArray)));
	
	            criteria.createCriteria("calendars")
	            .add(Restrictions.in("id", calendarIdArray));

	            result = criteria.list();
            }
            	        
        }
        else
        {
	        String rolesSQL = getRoleSQL(roles);
	        log.info("groups:" + groups.size());
	        String groupsSQL = getGroupsSQL(groups);
	        log.info("groupsSQL:" + groupsSQL);
	        //String sql = "select distinct event from Event event, Calendar c, Role cr, Group g where event.owningCalendar = c AND cr.calendar = c AND g.calendar = c AND event.stateId = ? AND event.startDateTime >= ? AND event.endDateTime <= ? " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by event.id";
	        String sql = "select distinct event from Event event, Calendar c, Role cr, Group g where event.owningCalendar = c AND cr.calendar = c AND g.calendar = c AND event.stateId = ? AND event.endDateTime >= ? " + (rolesSQL != null ? " AND cr.name IN " + rolesSQL : "") + (groupsSQL != null ? " AND g.name IN " + groupsSQL : "") + " order by event.startDateTime";
	        log.info("sql:" + sql);
	        Query q = session.createQuery(sql);
	        q.setInteger(0, stateId.intValue());
	        q.setCalendar(1, startDate);
	        log.info("startDate:" + startDate.getTime());
	        setRoleNames(2, q, roles);
	        /*
	        q.setCalendar(1, startDate);
	        q.setCalendar(2, endDate);
	        setRoleNames(3, q, roles);
	        */
	        setGroupNames(roles.size() + 2, q, groups);
	        
	        result = q.list();
        }
        
        log.info("result:" + result.size());
        
        Set set = new LinkedHashSet();
        set.addAll(result);	

        if(includeEventsCreatedByUser)
        {
            Criteria criteria = session.createCriteria(Event.class);
            criteria.add(Restrictions.eq("stateId", stateId));
            criteria.add(Restrictions.eq("creator", userName));
            criteria.add(Expression.gt("endDateTime", startDate));
            criteria.addOrder(Order.asc("startDateTime"));

            /*
            criteria.add(Expression.gt("endDateTime", endDate));
            criteria.add(Expression.lt("startDateTime", startDate));
            */
            
            set.addAll(criteria.list());	
        }
        List sortedList = new ArrayList();
        sortedList.addAll(set);
        
        Collections.sort(sortedList, new EventComparator());
        set.clear();
        
        set.addAll(sortedList);
        
        return set;
    }


    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getMyWorkingEventList(String userName, List roles, List groups, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_WORKING, false, true, session);
        
        return result;
    }

    
    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getWaitingEventList(String userName, List roles, List groups, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_PUBLISH, false, false, session);
        
        return result;
    }

    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getPublishedEventList(String userName, List roles, List groups, Long categoryId, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_PUBLISHED, false, true, session);
        
        if(categoryId != null)
        {
	        Iterator resultIterator = result.iterator();
	        while(resultIterator.hasNext())
	        {
	        	Event event = (Event)resultIterator.next();
	        	if(!getHasCategory(event, categoryId))
	        		resultIterator.remove();
	        }
        }
        
        return result;
    }

    public boolean getHasCategory(Event event, Long categoryId)
    {        
        Iterator i = event.getEventCategories().iterator();
        while(i.hasNext())
        {
            EventCategory eventCategory = (EventCategory)i.next();
            if(eventCategory.getCategory().getId().equals(categoryId))
                return true;
        }

        return false;
    }

    /**
     * Gets a list of all events available for a particular user which are in working mode.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getLinkedPublishedEventList(String userName, List roles, List groups, Long categoryId, Session session) throws Exception 
    {
        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.add(java.util.Calendar.YEAR, 5);
        
        Set result = getEventList(now, endDate, userName, roles, groups, Event.STATE_PUBLISHED, true, true, session);
        
        if(categoryId != null)
        {
	        Iterator resultIterator = result.iterator();
	        while(resultIterator.hasNext())
	        {
	        	Event event = (Event)resultIterator.next();
	        	if(!getHasCategory(event, categoryId))
	        		resultIterator.remove();
	        }
        }

        return result;
    }

    /**
     * This method returns a list of Events based on a number of parameters
     * @return List
     * @throws Exception
     */
    
    public Set getEventList(Long id, Integer stateId, java.util.Calendar startDate, java.util.Calendar endDate, Session session) throws Exception
    {
        Set list = null;
        
		Calendar calendar = CalendarController.getController().getCalendar(id, session);
		list = getEventList(calendar, stateId, startDate, endDate, session);
		
		return list;
    }
    
    /**
     * Gets a list of all events available for a particular calendar with the optional categories.
     * @return List of Event
     * @throws Exception
     */
    
    public Set getEventList(String[] calendarIds, String categoryAttribute, String[] categoryNames, Session session) throws Exception 
    {
        List result = null;
        
        String calendarSQL = null;
        if(calendarIds != null && calendarIds.length > 0)
        {
            calendarSQL = "(";
	        for(int i=0; i<calendarIds.length; i++)
	        {
	            String calendarIdString = calendarIds[i];

	            try
	            {
	                Integer calendarId = new Integer(calendarIdString);
	            }
	            catch(Exception e)
	            {
	                log.warn("An invalid calendarId was given:" + e.getMessage(), e);
	                return null;
	            }
	            
	            if(i > 0)
	                calendarSQL += ",";
	            
	            calendarSQL += calendarIdString;
	        }
	        calendarSQL += ")";
        }
        else
        {
            return null;
        }

        Object[] calendarIdArray = new Object[calendarIds.length];
        for(int i=0; i<calendarIds.length; i++)
            calendarIdArray[i] = new Long(calendarIds[i]);

        /*
        Object[] categoryNameArray = new Object[categoryNames.length];
        for(int i=0; i<categoryNames.length; i++)
            categoryNameArray[i] = new Long(categoryNames[i]);
            */
        Set set = new LinkedHashSet();

        if(calendarIdArray.length > 0)
        {
	        Criteria criteria = session.createCriteria(Event.class);
	        criteria.add(Expression.eq("stateId", Event.STATE_PUBLISHED));
	        //criteria.add(Expression.gt("startDateTime", java.util.Calendar.getInstance()));
	        criteria.add(Expression.gt("endDateTime", java.util.Calendar.getInstance()));
	        criteria.add(Expression.eq("stateId", Event.STATE_PUBLISHED));
	        criteria.addOrder(Order.asc("startDateTime"));
	        criteria.createCriteria("calendars")
	        .add(Expression.in("id", calendarIdArray));

	        Criteria eventCategoriesCriteria = null;
	        log.info("categoryAttribute:" + categoryAttribute);
	        if(categoryAttribute != null && !categoryAttribute.equalsIgnoreCase(""))
	        {
	            log.info("categoryAttribute:" + categoryAttribute);
	            eventCategoriesCriteria = criteria.createCriteria("eventCategories");
	            eventCategoriesCriteria.createCriteria("eventTypeCategoryAttribute")
	            .add(Expression.eq("internalName", categoryAttribute));
	        }

	        if(categoryNames.length > 0 && !categoryNames[0].equalsIgnoreCase(""))
	        {
	            log.info("categoryNames[0]:" + categoryNames[0]);
	            if(eventCategoriesCriteria == null)
		            eventCategoriesCriteria = criteria.createCriteria("eventCategories");

	            eventCategoriesCriteria.createCriteria("category")
	            .add(Expression.in("internalName", categoryNames));
	        }
	        
	        result = criteria.list();
        
	        log.info("result:" + result.size());
	        
	        set.addAll(result);	
        }
        
        
        return set;
    }
    
    /**
     * This method returns a list of Events based on a number of parameters within a transaction
     * @return List
     * @throws Exception
     */
    
    public Set getEventList(Calendar calendar, Integer stateId, java.util.Calendar startDate, java.util.Calendar endDate, Session session) throws Exception
    {
        Query q = session.createQuery("from Event as event inner join fetch event.owningCalendar as calendar where event.owningCalendar = ? AND event.stateId = ? AND event.startDateTime >= ? AND event.endDateTime <= ? order by event.startDateTime");
        q.setEntity(0, calendar);
        q.setInteger(1, stateId.intValue());
        q.setCalendar(2, startDate);
        q.setCalendar(3, endDate);
        
        List list = q.list();

        Set set = new LinkedHashSet();
        set.addAll(list);	

		return set;
    }


    /**
     * Gets a list of events fetched by name.
     * @return List of Event
     * @throws Exception
     */
    
    public List getEvent(String name, Session session) throws Exception 
    {
        List events = null;
        
        events = session.createQuery("from Event as event where event.name = ?").setString(0, name).list();
        
        return events;
    }
    
    
    /**
     * Deletes a event object in the database. Also cascades all events associated to it.
     * @throws Exception
     */
    
    public void deleteEvent(Long id, Session session) throws Exception 
    {
        Event event = this.getEvent(id, session);
        
        Iterator eventCategoriesIterator = event.getEventCategories().iterator();
        while(eventCategoriesIterator.hasNext())
        {
            EventCategory eventCategory = (EventCategory)eventCategoriesIterator.next();
            session.delete(eventCategory);
            eventCategoriesIterator.remove();
        }
        
        if(event.getStateId().equals(Event.STATE_PUBLISHED))
            new RemoteCacheUpdater().updateRemoteCaches(event.getCalendars());

        session.delete(event);
    }
    
    /**
     * Deletes a link to a event object in the database.
     * @throws Exception
     */
    
    public void deleteLinkedEvent(Long id, Long calendarId, Session session) throws Exception 
    {
        Event event = this.getEvent(id, session);
        Calendar calendar = CalendarController.getController().getCalendar(calendarId, session);
        event.getCalendars().remove(calendar);
        calendar.getEvents().remove(event);
        
		new RemoteCacheUpdater().updateRemoteCaches(calendarId);
    }
    
    /**
     * This method emails the owner of an event the new information and an address to visit.
     * @throws Exception
     */
    
    public void notifyPublisher(Event event, String publishEventUrl) throws Exception
    {
	    String email = "";
	    
	    try
	    {
	        List allPrincipals = new ArrayList();
	        Collection owningRoles = event.getOwningCalendar().getOwningRoles();
	        Iterator owningRolesIterator = owningRoles.iterator();
	        while(owningRolesIterator.hasNext())
	        {
	            Role role = (Role)owningRolesIterator.next();
	            List principals = RoleControllerProxy.getController().getInfoGluePrincipals(role.getName());
	            
	            Iterator userIterator = principals.iterator();
	            while(userIterator.hasNext())
	            {
	                InfoGluePrincipal principal = (InfoGluePrincipal)userIterator.next();
	                boolean hasGroup = hasUserGroup(principal, event);
	                if(hasGroup)
	                    allPrincipals.add(principal);
	            }
	        }

	        String addresses = "";
	        Iterator allPrincipalsIterator = allPrincipals.iterator();
	        while(allPrincipalsIterator.hasNext())
	        {
		        InfoGluePrincipal inforgluePrincipal = (InfoGluePrincipal)allPrincipalsIterator.next();
		        addresses += inforgluePrincipal.getEmail() + ";";
	        }

            String template;
	        
	        String contentType = PropertyHelper.getProperty("mail.contentType");
	        if(contentType == null || contentType.length() == 0)
	            contentType = "text/html";
	        
	        if(contentType.equalsIgnoreCase("text/plain"))
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventNotification_plain.vm"));
		    else
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventNotification_html.vm"));
		    
		    Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("publishEventUrl", publishEventUrl.replaceAll("\\{eventId\\}", event.getId().toString()));
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, template);
			email = tempString.toString();
	    
			String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

			log.info("Sending mail to:" + systemEmailSender + " and " + addresses);
			MailServiceFactory.getService().send(systemEmailSender, systemEmailSender, addresses, "InfoGlue Calendar - new event waiting", email, contentType, "UTF-8");
	    }
		catch(Exception e)
		{
			log.error("The notification was not sent. Reason:" + e.getMessage(), e);
		}
		
    }

    
    /**
     * This method emails the owner of an event the new information and an address to visit.
     * @throws Exception
     */
    
    public void notifySubscribers(Event event, String publishedEventUrl) throws Exception
    {
	    String subscriberEmails = PropertyHelper.getProperty("subscriberEmails");
	    
	    try
	    {
            String template;
	        
	        String contentType = PropertyHelper.getProperty("mail.contentType");
	        if(contentType == null || contentType.length() == 0)
	            contentType = "text/html";
	        
	        if(contentType.equalsIgnoreCase("text/plain"))
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventPublishedNotification_plain.vm"));
		    else
	            template = FileHelper.getFileAsString(new File(PropertyHelper.getProperty("contextRootPath") + "templates/newEventPublishedNotification_html.vm"));
		    
	        publishedEventUrl = publishedEventUrl.replaceAll("\\{j_username\\}", "fold1");
	        publishedEventUrl = publishedEventUrl.replaceAll("\\{j_password\\}", "fold2");
	        
	        Map parameters = new HashMap();
		    parameters.put("event", event);
		    parameters.put("publishedEventUrl", publishedEventUrl.replaceAll("\\{eventId\\}", event.getId().toString()));
		    
			StringWriter tempString = new StringWriter();
			PrintWriter pw = new PrintWriter(tempString);
			new VelocityTemplateProcessor().renderTemplate(parameters, pw, template);
			String email = tempString.toString();
	    
			String systemEmailSender = PropertyHelper.getProperty("systemEmailSender");
			if(systemEmailSender == null || systemEmailSender.equalsIgnoreCase(""))
				systemEmailSender = "infoglueCalendar@" + PropertyHelper.getProperty("mail.smtp.host");

			log.info("Sending mail to:" + systemEmailSender + " and " + subscriberEmails);
			MailServiceFactory.getService().send(systemEmailSender, systemEmailSender, subscriberEmails, "InfoGlue Calendar - new event published", email, contentType, "UTF-8");
	    
			String subscriberString = "";
			Set subscribers = event.getOwningCalendar().getSubscriptions();
			Iterator subscribersIterator = subscribers.iterator();
			while(subscribersIterator.hasNext())
			{
			    Subscriber subscriber = (Subscriber)subscribersIterator.next();

			    if(subscriberString.length() > 0)
			        subscriberString += ";";
		        
			    subscriberString += subscriber.getEmail();
			}

		    try
		    {
		        
				log.info("Sending mail to:" + systemEmailSender + " and " + subscriberString);
				MailServiceFactory.getService().send(systemEmailSender, systemEmailSender, subscriberString, "InfoGlue Calendar - new event published", email, contentType, "UTF-8");
		    }
			catch(Exception e)
			{
				log.error("The notification was not sent to persons. Reason:" + e.getMessage(), e);
			}

	    }
		catch(Exception e)
		{
			log.error("The notification was not sent. Reason:" + e.getMessage(), e);
		}
		
    }

    /**
     * This method checks if a user has one of the roles defined in the event.
     * @param principal
     * @param event
     * @return
     * @throws Exception
     */
    public boolean hasUserGroup(InfoGluePrincipal principal, Event event) throws Exception
    {
        Collection owningGroups = event.getOwningCalendar().getOwningGroups();
        if(owningGroups == null || owningGroups.size() == 0)
            return true;
        
        Iterator owningGroupsIterator = owningGroups.iterator();
        while(owningGroupsIterator.hasNext())
        {
            Group group = (Group)owningGroupsIterator.next();
            List principals = GroupControllerProxy.getController().getInfoGluePrincipals(group.getName());
            if(principals.contains(principal))
                return true;
        }
        
        return false;
    }
    
    public List getAssetKeys()
    {
        List assetKeys = new ArrayList();
        
        int i = 0;
        String assetKey = PropertyHelper.getProperty("assetKey." + i);
        while(assetKey != null && assetKey.length() > 0)
        {
            assetKeys.add(assetKey);
            
            i++;
            assetKey = PropertyHelper.getProperty("assetKey." + i);
        }
        
        return assetKeys;
    }


    private String getRoleSQL(List roles)
    {
        String rolesSQL = null;
        if(roles != null && roles.size() > 0)
        {
            rolesSQL = "(";
	        int i = 0;
	    	Iterator iterator = roles.iterator();
	        while(iterator.hasNext())
	        {
	            String roleName = (String)iterator.next();
	            
	            if(i > 0)
	                rolesSQL += ",";
	            
	            rolesSQL += "?";
	            i++;
	        }
	        rolesSQL += ")";
        }
   
        return rolesSQL;
    }
    
    private void setRoleNames(int index, Query q, List roles)
    {
        Iterator iterator = roles.iterator();
        while(iterator.hasNext())
        {
            String roleName = (String)iterator.next();
            log.info("roleName:" + roleName);
            q.setString(index, roleName);
            index++;
        }
    }

    private String getGroupsSQL(List groups)
    {
        String groupsSQL = null;
        if(groups != null && groups.size() > 0)
        {
            groupsSQL = "(";
	        int i = 0;
	    	Iterator iterator = groups.iterator();
	        while(iterator.hasNext())
	        {
	            String roleName = (String)iterator.next();
	            
	            if(i > 0)
	                groupsSQL += ",";
	            
	            groupsSQL += "?";
	            i++;
	        }
	        groupsSQL += ")";
        }
   
        return groupsSQL;
    }
    
    private void setGroupNames(int index, Query q, List groups)
    {
        Iterator iterator = groups.iterator();
        while(iterator.hasNext())
        {
            String groupName = (String)iterator.next();
            log.info("groupName:" + groupName);

            q.setString(index, groupName);
            index++;
        }
    }

}