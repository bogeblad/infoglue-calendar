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


/**
 * This class represents an generic Role in InfoGlue. It is used to identify a role no matter what source it was defined in.
 * 
 * @author Mattias Bogeblad
 */

public class InfoGlueRole 
{
	private final String name;
	private final String description;
	
	public InfoGlueRole(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}
	
	public String toString()
	{
		return "InfoGlueRole: " + name;
	}

	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof InfoGlueRole))
			return false;
		
		InfoGlueRole another = (InfoGlueRole)obj;
		return name.equals(another.getName());
	}

	public int hasCode()
	{
		return name.hashCode();
	}

}

