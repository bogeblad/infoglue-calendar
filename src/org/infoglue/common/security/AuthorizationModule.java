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

package org.infoglue.common.security;

import java.util.List;
import java.util.Properties;

import org.infoglue.common.exceptions.Bug;
import org.infoglue.common.exceptions.SystemException;

/**
 * This interface defines what a autorizationModule has to fulfill.
 * 
 * @author Mattias Bogeblad
 */

public interface AuthorizationModule
{
	/**
	 * Gets is the implementing class can update as well as read 
	 */
	
	public boolean getSupportUpdate();
	
	/**
	 * Gets is the implementing class can delete as well as read 
	 */
	
	public boolean getSupportDelete();
	
	/**
	 * Gets is the implementing class can create as well as read 
	 */
	
	public boolean getSupportCreate();

	/**
	 * Gets an authorized InfoGluePrincipal 
	 */
	
	public InfoGluePrincipal getAuthorizedInfoGluePrincipal(String userName) throws Exception;
	
	/**
	 * Gets an InfoGlueRole 
	 */
	
	public InfoGlueRole getAuthorizedInfoGlueRole(String roleName) throws Exception;

	/**
	 * This method is used to fetch a users roles.  
	 */

	public List authorizeUser(String userName) throws Exception;

	/**
	 * This method is used to fetch all available roles.  
	 */

	public List getRoles() throws Exception;

	/**
	 * This method is used to fetch all users.  
	 */

	public List getUsers() throws Exception;

	/**
	 * This method is used to fetch all users part of the named group.  
	 */

	public List getUsers(String roleName) throws Exception;
	
	/**
	 * This method is used to get a filtered list of all users.
	 * @param firstName
	 * @param lastName
	 * @param userName
	 * @param email
	 * @param roleIds
	 * @return
	 * @throws SystemException
	 * @throws Bug
	 */
	
	public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleIds) throws SystemException, Bug;

	/**
	 * This method is used to create a new user.  
	 */

	public void createInfoGluePrincipal(String userName, String password, String firstName, String lastName, String email) throws Exception;

	/**
	 * This method is used to update an existing user.  
	 */

	public void updateInfoGluePrincipal(String userName, String password, String firstName, String lastName, String email, String[] roleNames) throws Exception;

	/**
	 * This method is used to send out a newpassword to an existing users.  
	 */

	public void updateInfoGluePrincipalPassword(String userName) throws Exception;

	/**
	 * This method is used to delete an existing user.  
	 */

	public void deleteInfoGluePrincipal(String userName) throws Exception;


	/**
	 * This method is used to create a new rol.  
	 */

	public void createInfoGlueRole(String roleName, String description) throws Exception;

	/**
	 * This method is used to update an existing role.  
	 */

	public void updateInfoGlueRole(String roleName, String description, String[] userNames) throws Exception;

	/**
	 * This method is used to delete an existing role.  
	 */

	public void deleteInfoGlueRole(String roleName) throws Exception;


	public Properties getExtraProperties();

	public void setExtraProperties(Properties properties);
}