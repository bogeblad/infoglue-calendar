package org.infoglue.calendar.util.validators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.infoglue.calendar.controllers.EntryController;
import org.infoglue.common.contenttypeeditor.controllers.ContentTypeDefinitionController;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;
import org.infoglue.cms.entities.management.ContentTypeAttribute;


public class EntryBean implements java.util.Map 
{
	private java.util.Map delegate = new HashMap();
	
	public EntryBean(ContentTypeDefinition contentTypeDefinition, org.infoglue.calendar.entities.Entry entry) 
	{
		List contentTypeAttributes = ContentTypeDefinitionController.getController().getContentTypeAttributes(contentTypeDefinition.getSchemaValue());
		for(Iterator i=contentTypeAttributes.iterator(); i.hasNext();) 
		{
			ContentTypeAttribute attribute = (ContentTypeAttribute) i.next();
			String name  = attribute.getName();
			String value = EntryController.getController().getAttributeValue(entry, name, false);
			delegate.put(name, value);
		}
	}
	  
	  // -- MAP ---
	public Object get(Object key) { return delegate.get(key); }
	public int size() { return delegate.size(); }
	public boolean isEmpty() { return delegate.isEmpty(); }
	public boolean containsKey(Object key) { return delegate.containsKey(key); }
	public boolean containsValue(Object value) { return delegate.containsValue(value); }
	public Object put(Object key, Object value) { return null; }
	public void putAll(java.util.Map t) {}
	public Object remove(Object key) { return null; }
	public void clear() {}
	public Set keySet() { return delegate.keySet(); }
	public Collection values() { return delegate.values(); }
	public Set entrySet() { return delegate.entrySet(); }
	public boolean equals(Object o) { return delegate.equals(o); }
	public int hashCode() { return delegate.hashCode(); }
}
