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

import java.util.ArrayList;
import java.util.List;

import org.infoglue.cms.entities.kernel.BaseEntityVO;
import org.infoglue.cms.entities.management.SystemUserVO;
import org.infoglue.cms.exception.ConstraintException;
import org.infoglue.cms.exception.SystemException;
import org.infoglue.cms.security.AuthorizationModule;
import org.infoglue.cms.security.InfoGlueAuthenticationFilter;
import org.infoglue.cms.security.InfoGluePrincipal;
import org.infoglue.cms.util.CmsLogger;

/**
 * @author Mattias Bogeblad
 * 
 * This class acts as the proxy for getting the right roles.
 */

public class UserControllerProxy
{
	private static AuthorizationModule authorizationModule = null;
	
	public static UserControllerProxy getController()
	{
		return new UserControllerProxy();
	}
	
	/**
	 * This method instantiates the AuthorizationModule.
	 */
	
	public AuthorizationModule getAuthorizationModule() throws SystemException
	{
		if(authorizationModule == null)
		{
			try
	    	{
			    CmsLogger.logInfo("InfoGlueAuthenticationFilter.authorizerClass:" + InfoGlueAuthenticationFilter.authorizerClass);
				authorizationModule = (AuthorizationModule)Class.forName(InfoGlueAuthenticationFilter.authorizerClass).newInstance();
				CmsLogger.logInfo("authorizationModule:" + authorizationModule);
				authorizationModule.setExtraProperties(InfoGlueAuthenticationFilter.extraProperties);
				CmsLogger.logInfo("InfoGlueAuthenticationFilter.extraProperties:" + InfoGlueAuthenticationFilter.extraProperties);
	    	}
	    	catch(Exception e)
	    	{
	    		//e.printStackTrace();
	    		CmsLogger.logSevere("There was an error initializing the authorizerClass:" + e.getMessage(), e);
	    		throw new SystemException("There was an error initializing the authorizerClass:" + e.getMessage(), e);
	    	}
		}
	   
		return authorizationModule;
	}
	
	/**
	 * This method return whether the module in question supports updates to the values.
	 */
	
	public boolean getSupportUpdate() throws ConstraintException, SystemException, Exception
	{
		return getAuthorizationModule().getSupportUpdate();
	}

	/**
	 * This method return whether the module in question supports deletes of users.
	 */
	
	public boolean getSupportDelete() throws ConstraintException, SystemException, Exception
	{
		return getAuthorizationModule().getSupportDelete();
	}

	/**
	 * This method return whether the module in question supports creation of new users.
	 */
	
	public boolean getSupportCreate() throws ConstraintException, SystemException, Exception
	{
		return getAuthorizationModule().getSupportCreate();
	}

	/**
	 * This method returns a complete list of available users
	 */
	
    public List getAllUsers() throws ConstraintException, SystemException, Exception
    {
    	List users = new ArrayList();
    	
		users = getAuthorizationModule().getUsers();
    	
    	return users;
    }

	/**
	 * This method returns a list of all sought for users
	 */
	
    public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleNames) throws ConstraintException, SystemException
    {
    	List users = new ArrayList();
    	
		users = getAuthorizationModule().getFilteredUsers(firstName, lastName, userName, email, roleNames);
    	
    	return users;
    }
    
	/**
	 * This method returns a certain user
	 */
	
    public InfoGluePrincipal getUser(String userName) throws ConstraintException, SystemException, Exception
    {
    	InfoGluePrincipal infoGluePrincipal = null;
    	
		infoGluePrincipal = getAuthorizationModule().getAuthorizedInfoGluePrincipal(userName);
    	
    	return infoGluePrincipal;
    }
    
    
	/**
	 * This method creates a new user
	 */
	
	public InfoGluePrincipal createUser(SystemUserVO systemUserVO) throws ConstraintException, SystemException, Exception
	{
		InfoGluePrincipal infoGluePrincipal = null;
    	
		getAuthorizationModule().createInfoGluePrincipal(systemUserVO);
    	
		return getUser(systemUserVO.getUserName());
	}

	/**
	 * This method updates an existing user
	 */
	
	public void updateUser(SystemUserVO systemUserVO, String[] roleNames) throws ConstraintException, SystemException, Exception
	{
		getAuthorizationModule().updateInfoGluePrincipal(systemUserVO, roleNames);
	}

	/**
	 * This method makes a new password and sends it to the user
	 */
	
	public void updateUserPassword(String userName) throws ConstraintException, SystemException, Exception
	{
		getAuthorizationModule().updateInfoGluePrincipalPassword(userName);
	}

	/**
	 * This method deletes an existing user
	 */
	
	public void deleteUser(String userName) throws ConstraintException, SystemException, Exception
	{
		getAuthorizationModule().deleteInfoGluePrincipal(userName);
	}
	
	public BaseEntityVO getNewVO()
	{
		return null;
	}
 
}
