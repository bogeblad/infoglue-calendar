package org.infoglue.calendar.util.validators;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.infoglue.calendar.entities.Entry;
import org.infoglue.common.contenttypeeditor.entities.ContentTypeDefinition;
import org.infoglue.common.exceptions.ConstraintException;
import org.infoglue.common.util.ConstraintExceptionBuffer;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.validator.ValidationException;

public class EntryValidator {
	
	/**
	 * 
	 */
	public EntryValidator() {}

	/**
	 * 
	 */
	public ConstraintExceptionBuffer validate(ContentTypeDefinition contentTypeDefinition, Entry entry) 
	{
		try 
		{
			EntryBean bean = new EntryBean(contentTypeDefinition, entry);
			ValidatorResources resources = loadResources(contentTypeDefinition);
			Validator validator = new Validator(resources, "requiredForm");
			validator.setOnlyReturnErrors(true);
			validator.setParameter(Validator.BEAN_PARAM, bean);
			ValidatorResults results = validator.validate();
			if(results.isEmpty())
				return new ConstraintExceptionBuffer();
			else
				return populateConstraintExceptionBuffer(results);
		} 
		catch(Exception e) 
		{
			return new ConstraintExceptionBuffer();
		}
	}

	/**
	 * 
	 */
	private static ConstraintExceptionBuffer populateConstraintExceptionBuffer(ValidatorResults results) 
	{
		/*
        if(this.getFieldErrors() != null && this.getFieldErrors().size() > 0)
        {
            ActionContext.getContext().getValueStack().getContext().put("actionErrors", this.getActionErrors());
            ActionContext.getContext().getValueStack().getContext().put("fieldErrors", this.getFieldErrors());
            ActionContext.getContext().getValueStack().getContext().put("errorAction", this);
            
            log.warn("actionErrors:" + this.getActionErrors());
            log.warn("fieldErrors:" + this.getFieldErrors());
            log.warn("errorAction:" + this);
            throw new ValidationException("An validation error occurred - more information is in the valuestack...");
        }
        */
		ConstraintExceptionBuffer ceb = new ConstraintExceptionBuffer();
		Set s = results.getPropertyNames();
		
		for(Iterator i=s.iterator(); i.hasNext(); ) 
		{
			ValidatorResult r = results.getValidatorResult((String) i.next());
			Field field       = r.getField();
			String name       = field.getKey();
			for(Iterator messages=field.getMessages().values().iterator(); messages.hasNext();) 
			{
				Msg m = (Msg) messages.next();
				System.out.println("Adding error message:" + name + "=" + m.getKey());
				ceb.add(new ConstraintException(name, m.getKey()));
			}
		}
		return ceb;
	}

	/**
	 * 
	 */
    private ValidatorResources loadResources(ContentTypeDefinition contentTypeDefinition) 
    {
		try 
		{
			InputStream is = readValidatorXML(contentTypeDefinition);
			return new ValidatorResources(is);
		} 
		catch(Exception e) 
		{
			System.out.println(e.toString());
		}
		return null;
    }
	
	/**
	 * TODO: remove - read from ContentTypeDefinition
	 */
	private InputStream readValidatorXML(ContentTypeDefinition contentTypeDefinition) throws Exception
	{
		String xml = contentTypeDefinition.getSchemaValue();
		String validationSchema = xml.substring(xml.indexOf("<form-validation>"), xml.indexOf("</form-validation>") + 18);
		
		return new ByteArrayInputStream(validationSchema.getBytes("UTF-8"));		 
	}
}
