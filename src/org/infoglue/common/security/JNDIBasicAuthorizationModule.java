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
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infoglue.common.exceptions.Bug;
import org.infoglue.common.exceptions.SystemException;
import org.infoglue.common.util.PropertyHelper;

/**
 * @author Mattias Bogeblad
 *
 * This authentication module authenticates an user against the ordinary infoglue database.
 */

public class JNDIBasicAuthorizationModule implements AuthorizationModule
{
    private static final Log log = LogFactory.getLog(JNDIBasicAuthorizationModule.class);
    
	protected Properties extraProperties = null;
	
	/**
	 * Gets is the implementing class can update as well as read 
	 */
	
	public boolean getSupportUpdate() 
	{
		return false;
	}
	
	/**
	 * Gets is the implementing class can delete as well as read 
	 */
	
	public boolean getSupportDelete()
	{
		return false;
	}
	
	/**
	 * Gets is the implementing class can create as well as read 
	 */
	
	public boolean getSupportCreate()
	{
		return false;
	}
	
	/**
	 * Gets an authorized InfoGluePrincipal 
	 */
	public InfoGluePrincipal getAuthorizedInfoGluePrincipal(String userName) throws Exception
	{
		InfoGluePrincipal infogluePrincipal = null;
		
		String administratorUserName = PropertyHelper.getProperty("administratorUserName");
		String administratorEmail 	 = PropertyHelper.getProperty("administratorEmail");
		
		final boolean isAdministrator = userName.equalsIgnoreCase(administratorUserName) ? true : false;
		if(isAdministrator)
		{
			infogluePrincipal = new InfoGluePrincipal(userName, "System", "Administrator", administratorEmail, new ArrayList(), isAdministrator);
		}
		else
		{	
			Map userAttributes = getUserAttributes(userName);
			List roles = getRoles(userName);
			
			infogluePrincipal = new InfoGluePrincipal(userName, (String)userAttributes.get("firstName"), (String)userAttributes.get("lastName"), (String)userAttributes.get("mail"), roles, isAdministrator);
		}
		
		return infogluePrincipal;
	}
	
	/**
	 * Gets an authorized InfoGlueRole.
	 */
	
	public InfoGlueRole getAuthorizedInfoGlueRole(String roleName) throws Exception
	{
		InfoGlueRole infoglueRole = null;

		Collection roles = getRoles();
		Iterator rolesIterator = roles.iterator();
		while(rolesIterator.hasNext())
		{
			InfoGlueRole candidate = (InfoGlueRole)rolesIterator.next();
			if(candidate.getName().equals(roleName))
			{
				infoglueRole = candidate;
				break;
			}
		}
				
		return infoglueRole;
	}
	
	/**
	 * This method gets a users roles
	 */
	
	public List authorizeUser(String userName) throws Exception
	{
		return getRoles(userName);
	}

	
	/**
	 * Returns an attribute set which this user has. 
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected Map getUserAttributes(String userName) throws NamingException 
	{
		Map userAttributes = new HashMap();
		
		DirContext ctx 		= null;
		
		String connectionURL 		= this.extraProperties.getProperty("connectionURL");
		String connectionName		= this.extraProperties.getProperty("connectionName");
		String connectionPassword	= this.extraProperties.getProperty("connectionPassword");
		String roleBase 			= this.extraProperties.getProperty("roleBase");
		String userBase				= this.extraProperties.getProperty("userBase");
		String userSearch			= this.extraProperties.getProperty("userSearch");
		String userAttributesFilter	= this.extraProperties.getProperty("userAttributesFilter");
		
		String userNameAttributeFilter	= this.extraProperties.getProperty("userNameAttributeFilter", "name");
		String userFirstNameAttributeFilter	= this.extraProperties.getProperty("userFirstNameAttributeFilter", "givenName");
		String userLastNameAttributeFilter	= this.extraProperties.getProperty("userLastNameAttributeFilter", "sn");
		String userMailAttributeFilter	= this.extraProperties.getProperty("userMailAttributeFilter", "mail");
		String memberOfAttributeFilter	= this.extraProperties.getProperty("memberOfAttributeFilter", "memberOf");
		String roleFilter				= this.extraProperties.getProperty("roleFilter", "InfoGlue");

		
		this.extraProperties.list(System.out);
		log.debug("connectionURL:" + connectionURL);
		log.debug("connectionName:" + connectionName);
		log.debug("connectionPassword:" + connectionPassword);
		
		// Create a Hashtable object.
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, connectionURL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, connectionName);
		env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
		
		try 
		{
			ctx = new InitialDirContext(env); 

			String baseDN = userBase;
			String searchFilter = "(CN=" + userName +")";
			if(userSearch != null && userSearch.length() > 0)
				searchFilter = userSearch.replaceAll("\\{1\\}", userName);
			
			String attributesFilter = "name, givenName, sn, mail, memberOf";
			if(userAttributesFilter != null && userAttributesFilter.length() > 0)
				attributesFilter = userAttributesFilter;
			
			String[] attrID = attributesFilter.split(",");
			
			log.debug("baseDN:" + baseDN);
			log.debug("searchFilter:" + searchFilter);
			log.debug("attrID" + attrID);
						
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
			
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 
			if(!answer.hasMore())
				throw new Exception("The user with userName=" + userName + " was not found in the JNDI Data Source.");
				
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				log.debug("Person:" + sr.toString() + "\n");
				Attributes attributes = sr.getAttributes();
				
				for(int i=0; i<attrID.length; i++)
				{
					Attribute attribute = attributes.get(attrID[i]);
					log.debug("attribute:" + attribute.toString());
					NamingEnumeration allEnum = attribute.getAll();
					while(allEnum.hasMore())
					{
						String value = (String)allEnum.next();
						log.debug("value:" + value);
						userAttributes.put(attrID[i], value);
					}
				}	
				
				Attribute userNameAttribute = attributes.get(userNameAttributeFilter);
				log.debug("userNameAttribute:" + userNameAttribute.toString());
				Attribute userFirstNameAttribute = attributes.get(userFirstNameAttributeFilter);
				log.debug("userFirstNameAttribute:" + userFirstNameAttribute.toString());
				Attribute userLastNameAttribute = attributes.get(userLastNameAttributeFilter);
				log.debug("userLastNameAttribute:" + userLastNameAttribute.toString());
				Attribute userMailAttribute = attributes.get(userMailAttributeFilter);
				log.debug("userMailAttribute:" + userMailAttribute.toString());
				
				userAttributes.put("firstName", userFirstNameAttribute.get().toString());
				userAttributes.put("lastName", userLastNameAttribute.get().toString());
				//userAttributes.put("firstName", userFirstNameAttribute);
				//userAttributes.put("firstName", userFirstNameAttribute);
				//Attribute memberOfAttribute = attributes.get(memberOfAttributeFilter);
				//log.debug("memberOfAttribute:" + memberOfAttribute.toString());

			} 
			ctx.close();
		}
		catch (Exception e) 
		{
			log.debug("Could not find attributes for user: " +userName +e);
			e.printStackTrace();
		}

		return userAttributes;
	}
	
	/**
	 * Return a List of roles associated with the given User. Any
	 * roles present in the user's directory entry are supplemented by
	 * a directory search. If no roles are associated with this user,
	 * a zero-length List is returned.
	 *
	 * @param context The directory context we are searching
	 * @param user The User to be checked
	 *
	 * @exception NamingException if a directory server error occurs
	 */
	
	protected List getRoles(String userName) throws NamingException 
	{
		log.debug("**************************************************");
		log.debug("*In JNDI version								 *");
		log.debug("**************************************************");
		log.debug("userName:" + userName);
		
		List roles = new ArrayList();
		
		DirContext ctx 		= null;
		
		String connectionURL 		= this.extraProperties.getProperty("connectionURL");
		String connectionName		= this.extraProperties.getProperty("connectionName");
		String connectionPassword	= this.extraProperties.getProperty("connectionPassword");
		String roleBase 			= this.extraProperties.getProperty("roleBase");
		String userBase				= this.extraProperties.getProperty("userBase");
		String userSearch			= this.extraProperties.getProperty("userSearch");
		String memberOfAttribute	= this.extraProperties.getProperty("memberOfAttributeFilter");
		String rolesAttributeFilter = this.extraProperties.getProperty("rolesAttributesFilter");
		String roleNameAttribute 	= this.extraProperties.getProperty("roleNameAttribute");
		String roleFilter			= this.extraProperties.getProperty("roleFilter", "InfoGlue");

		log.debug("connectionURL:" + connectionURL);
		log.debug("connectionName:" + connectionName);
		log.debug("connectionPassword:" + connectionPassword);
		log.debug("roleBase:" + roleBase);
		log.debug("userBase:" + userBase);
		
		// Create a Hashtable object.
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, connectionURL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, connectionName);
		env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
		
		try 
		{
			ctx = new InitialDirContext(env); 

			String baseDN = userBase;
			String searchFilter = "(CN=" + userName +")";
			if(userSearch != null && userSearch.length() > 0)
				searchFilter = userSearch.replaceAll("\\{1\\}", userName);
			
			String memberOfAttributeFilter = "memberOf";
			if(memberOfAttribute != null && memberOfAttribute.length() > 0)
			    memberOfAttributeFilter = memberOfAttribute;
			
			String[] attrID = memberOfAttributeFilter.split(",");
			
			String rolesAttribute = "distinguishedName";
			if(rolesAttributeFilter != null && rolesAttributeFilter.length() > 0)
				rolesAttribute = rolesAttributeFilter;
			
			log.debug("baseDN:" + baseDN);
			log.debug("searchFilter:" + searchFilter);
			log.debug("attrID" + attrID);
			
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
			
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 
			if(!answer.hasMore())
				throw new Exception("The user with userName=" + userName + " was not found in the JNDI Data Source.");
				
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				log.debug("Person:" + sr.toString() + "\n");
				Attributes attributes = sr.getAttributes();
				
				Attribute attribute = attributes.get(memberOfAttributeFilter);
				log.debug("..................attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					Object groupNameObject = allEnum.next();
					
					String groupName = groupNameObject.toString();
					log.debug("groupName:" + groupName);
					log.debug("roleBase:" + roleBase);
					log.debug("indexOf:" + groupName.indexOf(roleBase));
					if(roleBase != null && groupName.indexOf(roleBase) > -1)
					{
					    groupName = groupName.substring(0, groupName.indexOf(roleBase));
					    groupName = groupName.substring(0, groupName.lastIndexOf(","));
					}
					log.debug("roleNameAttribute:" + roleNameAttribute);
					log.debug("groupName:" + groupName);
					log.debug("indexOf:" + groupName.indexOf(roleNameAttribute));
					if(roleNameAttribute != null && groupName.indexOf(roleNameAttribute) > -1)
					{
					    groupName = groupName.substring(groupName.indexOf(roleNameAttribute) + roleNameAttribute.length() + 1);
					}
					
					log.debug("groupName:" + groupName);
					log.debug("groupName:" + groupName);
					if(roleFilter.equalsIgnoreCase("*") || groupName.indexOf(roleFilter) > -1)
					{
					    InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
						log.debug("Adding role.................:" + groupName);
						roles.add(infoGlueRole);
					}
				}
				
			} 
			ctx.close();
		}
		catch (Exception e) 
		{
			log.debug("Could not find Group for empID: " +userName +e);
			e.printStackTrace();
		}

		return roles;
	}

	/**
	 * This method gets a list of roles
	 */
	
	public List getRoles() throws Exception
	{
	    log.debug("getRoles start....");
		List roles = new ArrayList();
		
		DirContext ctx 		= null;
		
		String connectionURL 		= this.extraProperties.getProperty("connectionURL");
		String connectionName		= this.extraProperties.getProperty("connectionName");
		String connectionPassword	= this.extraProperties.getProperty("connectionPassword");
		String roleBase 			= this.extraProperties.getProperty("roleBase");
		String rolesFilter 			= this.extraProperties.getProperty("rolesFilter");
		String rolesAttributeFilter = this.extraProperties.getProperty("rolesAttributesFilter");
		String roleNameAttribute 	= this.extraProperties.getProperty("roleNameAttribute");
		
		// Create a Hashtable object.
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, connectionURL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, connectionName);
		env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
		

		try 
		{
			ctx = new InitialDirContext(env); 
		
			String baseDN = roleBase;
			String searchFilter = "(cn=InfoGlue*)";
			if(rolesFilter != null && rolesFilter.length() > 0)
				searchFilter = rolesFilter;
			
			String rolesAttribute = "distinguishedName";
			if(rolesAttributeFilter != null && rolesAttributeFilter.length() > 0)
				rolesAttribute = rolesAttributeFilter;
	
			String[] attrID = rolesAttribute.split(",");
			log.debug("attrID:" + attrID);
			
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no groups found in the JNDI Data Source.");
		
			log.debug("-----------------------\n");
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				log.debug("Group:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				log.debug("attributes:" + attributes.toString());
				log.debug("roleNameAttribute:" + roleNameAttribute);
				Attribute attribute = attributes.get(roleNameAttribute);
				log.debug("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String groupName = (String)allEnum.next();
					log.debug("groupName:" + groupName);
					
					InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
					roles.add(infoGlueRole);
				}
				
			} 
			log.debug("-----------------------\n");

			ctx.close();
		}
		catch (Exception e) 
		{
			log.debug("Could not find Groups: " + e.getMessage());
		}
	    log.debug("getRoles end....");

		return roles;
	}

	/**
	 * This method gets a list of users
	 */
	
	public List getUsers() throws Exception
	{
	    log.debug("getUsers start...");
	    
		List users = new ArrayList();
		
		DirContext ctx 		= null;
		
		String connectionURL 			= this.extraProperties.getProperty("connectionURL");
		String connectionName			= this.extraProperties.getProperty("connectionName");
		String connectionPassword		= this.extraProperties.getProperty("connectionPassword");
		String roleBase 				= this.extraProperties.getProperty("roleBase");
		String userBase					= this.extraProperties.getProperty("userBase");
		String userListSearch			= this.extraProperties.getProperty("userListSearch");
		String userAttributesFilter		= this.extraProperties.getProperty("userAttributesFilter");
		String userNameAttributeFilter	= this.extraProperties.getProperty("userNameAttributeFilter", "name");
		String userFirstNameAttributeFilter	= this.extraProperties.getProperty("userFirstNameAttributeFilter", "givenName");
		String userLastNameAttributeFilter	= this.extraProperties.getProperty("userLastNameAttributeFilter", "sn");
		String userMailAttributeFilter	= this.extraProperties.getProperty("userMailAttributeFilter", "mail");
		String memberOfAttributeFilter	= this.extraProperties.getProperty("memberOfAttributeFilter", "memberOf");
		String roleFilter				= this.extraProperties.getProperty("roleFilter", "InfoGlue");
		String roleNameAttribute 		= this.extraProperties.getProperty("roleNameAttribute");

		// Create a Hashtable object.
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, connectionURL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, connectionName);
		env.put(Context.SECURITY_CREDENTIALS, connectionPassword);

		try 
		{
			ctx = new InitialDirContext(env); 
		
			String baseDN = userBase;
			String searchFilter = "(CN=*)";
			if(userListSearch != null && userListSearch.length() > 0)
				searchFilter = userListSearch;
			
			String attributesFilter = "name, givenName, sn, mail, memberOf";
			if(userAttributesFilter != null && userAttributesFilter.length() > 0)
				attributesFilter = userAttributesFilter;
						
			String[] attrID = attributesFilter.split(",");
			
			log.debug("baseDN:" + baseDN);
			log.debug("searchFilter:" + searchFilter);
			log.debug("attrID" + attrID);
						
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no users found in the JNDI Data Source.");
		
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				log.debug("Person:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				log.debug("attributes:" + attributes.toString());
				Attribute userNameAttribute = attributes.get(userNameAttributeFilter);
				log.debug("userNameAttribute:" + userNameAttribute.toString());
				Attribute userFirstNameAttribute = attributes.get(userFirstNameAttributeFilter);
				log.debug("userFirstNameAttribute:" + userFirstNameAttribute.toString());
				Attribute userLastNameAttribute = attributes.get(userLastNameAttributeFilter);
				log.debug("userLastNameAttribute:" + userLastNameAttribute.toString());
				Attribute userMailAttribute = attributes.get(userMailAttributeFilter);
				log.debug("userMailAttribute:" + userMailAttribute.toString());
				
				Attribute memberOfAttribute = attributes.get(memberOfAttributeFilter);
				log.debug("memberOfAttribute:" + memberOfAttribute.toString());
				
				List roles = new ArrayList();
				NamingEnumeration allEnum = memberOfAttribute.getAll();
				while(allEnum.hasMore())
				{
					String groupName = (String)allEnum.next();
					log.debug("groupName:" + groupName);
					log.debug("roleBase:" + roleBase);
					if(roleBase != null && groupName.indexOf(roleBase) > -1)
					{
					    groupName = groupName.substring(0, groupName.indexOf(roleBase));
					    groupName = groupName.substring(0, groupName.lastIndexOf(","));
					}
					
					log.debug("groupName:" + groupName);
					if(roleFilter.equalsIgnoreCase("*") || groupName.indexOf(roleFilter) > -1)
					{
					    log.debug("roleNameAttribute:" + roleNameAttribute);
						log.debug("groupName:" + groupName);
						log.debug("indexOf:" + groupName.indexOf(roleNameAttribute));
						if(roleNameAttribute != null && groupName.indexOf(roleNameAttribute) > -1)
						{
						    groupName = groupName.substring(groupName.indexOf(roleNameAttribute) + roleNameAttribute.length() + 1);
						}
						
					    InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
					    roles.add(infoGlueRole);
					}
				}
				
				InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(userNameAttribute.get().toString(), userFirstNameAttribute.get().toString(), userLastNameAttribute.get().toString(), userMailAttribute.get().toString(), roles, false);
				users.add(infoGluePrincipal);
				
			} 
			ctx.close();
		}
		catch (Exception e) 
		{
			log.debug("Could not find Groups: " + e.getMessage());
		}
	    log.debug("getUsers end...");

		return users;
	}
	
	public List getFilteredUsers(String firstName, String lastName, String userName, String email, String[] roleIds) throws SystemException, Bug
	{
		List users = new ArrayList();
		//TODO		
		return users;
	}

	public List getUsers(String roleName) throws Exception
	{
	    log.debug("--------getUsers(String roleName) start---------------");
		List users = new ArrayList();

		DirContext ctx 		= null;
		
		String connectionURL 		= this.extraProperties.getProperty("connectionURL");
		String connectionName		= this.extraProperties.getProperty("connectionName");
		String connectionPassword	= this.extraProperties.getProperty("connectionPassword");
		String roleBase 			= this.extraProperties.getProperty("roleBase");
		String rolesFilter 			= this.extraProperties.getProperty("rolesFilter");
		String rolesAttributeFilter = this.extraProperties.getProperty("rolesAttributesFilter");
		String roleNameAttribute	= this.extraProperties.getProperty("roleNameAttribute");
		String usersAttributeFilter = this.extraProperties.getProperty("usersAttributesFilter");
		String userNameAttribute	= this.extraProperties.getProperty("userNameAttributeFilter");
		String userBase 			= this.extraProperties.getProperty("userBase");
		
		// Create a Hashtable object.
		Hashtable env = new Hashtable();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, connectionURL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, connectionName);
		env.put(Context.SECURITY_CREDENTIALS, connectionPassword);
		

		try 
		{
			ctx = new InitialDirContext(env); 
		
			String baseDN = roleBase;
			String searchFilter = "(cn=InfoGlue*)";
			if(rolesFilter != null && rolesFilter.length() > 0)
				searchFilter = rolesFilter;
			
			String rolesAttribute = "distinguishedName";
			if(rolesAttributeFilter != null && rolesAttributeFilter.length() > 0)
				rolesAttribute = rolesAttributeFilter;
	
			String[] attrID = rolesAttribute.split(",");
			
			SearchControls ctls = new SearchControls(); 
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setReturningAttributes(attrID);
	
			NamingEnumeration answer = ctx.search(baseDN, searchFilter, ctls); 

			if(!answer.hasMore())
				throw new Exception("The was no groups found in the JNDI Data Source.");
		
			while (answer.hasMore()) 
			{
				SearchResult sr = (SearchResult)answer.next();
				log.debug("Group:" + sr.toString() + "\n");
				
				Attributes attributes = sr.getAttributes();
				log.debug("attributes:" + attributes.toString());
				log.debug("roleNameAttribute:" + roleNameAttribute);
				Attribute attribute = attributes.get(roleNameAttribute);
				log.debug("attribute:" + attribute.toString());
				NamingEnumeration allEnum = attribute.getAll();
				while(allEnum.hasMore())
				{
					String groupName = (String)allEnum.next();
					log.debug("groupName:" + groupName);
					
					if(groupName.equals(roleName))
					{
					    Attribute usersAttribute = attributes.get(usersAttributeFilter);
						log.debug("usersAttribute:" + usersAttribute.toString());
						
						List roles = new ArrayList();
						NamingEnumeration allUsersEnum = usersAttribute.getAll();
						while(allUsersEnum.hasMore())
						{
							String userName = (String)allUsersEnum.next();
							log.debug("userName:" + userName);
							log.debug("userBase:" + userBase);
							
							if(roleBase != null && userName.indexOf(userBase) > -1)
							{
							    userName = userName.substring(0, userName.indexOf(userBase));
							    userName = userName.substring(0, userName.lastIndexOf(","));
							}
							
							log.debug("userNameAttribute:" + userNameAttribute);
							log.debug("groupName:" + userName);
							log.debug("indexOf:" + userName.indexOf(userNameAttribute));
							if(roleNameAttribute != null && userName.indexOf(userNameAttribute) > -1)
							{
							    userName = userName.substring(userName.indexOf(userNameAttribute) + userNameAttribute.length() + 1);
							}
							
							InfoGluePrincipal infoGluePrincipal = new InfoGluePrincipal(userName, "", "", "", new ArrayList(), false);
						    users.add(infoGluePrincipal);
						}
						
					    //InfoGlueRole infoGlueRole = new InfoGlueRole(groupName, "Not available from JNDI-source");
						//users.add(infoGluePrincipal);
					}
				}
				
			} 
			ctx.close();
		}
		catch (Exception e) 
		{
			log.debug("Could not find Groups: " + e.getMessage());
		}
	    log.debug("--------------------END---------------------");

		return users;
	}

	public void createInfoGluePrincipal(SystemUserVO systemUserVO) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support creation of users yet...");
	}

	public void updateInfoGluePrincipal(SystemUserVO systemUserVO, String[] roleNames) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support updating of users yet...");
	}

	public void updateInfoGluePrincipalPassword(String userName) throws Exception 
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support updates of users yet...");
	}

	public void deleteInfoGluePrincipal(String userName) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support deletion of users yet...");
	}
	
	public void createInfoGlueRole(RoleVO roleVO) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support creation of users yet...");
	}

	public void updateInfoGlueRole(RoleVO roleVO, String[] userNames) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support updates of users yet...");
	}

	public void deleteInfoGlueRole(String roleName) throws Exception
	{
		throw new SystemException("The JNDI BASIC Authorization module does not support deletion of roles yet...");
	}
	
	public Properties getExtraProperties()
	{
		return this.extraProperties;
	}

	public void setExtraProperties(Properties properties)
	{
		this.extraProperties = properties;
	}


}
