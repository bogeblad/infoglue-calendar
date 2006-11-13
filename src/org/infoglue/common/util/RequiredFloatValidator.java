/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.infoglue.common.util;

import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.validators.FieldValidatorSupport;


/**
 * RequiredStringValidator checks that a Float field is non-null
 * (i.e. it isn't "").  The "trim" parameter determines whether it will {@link String#trim() trim}
 * the String before performing the length check.  If unspecified, the String will be trimmed.
 */
public class RequiredFloatValidator extends FieldValidatorSupport 
{
    public void validate(Object object) throws ValidationException 
    {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        
        if (!(value instanceof Float || value instanceof Float[])) 
        {
        	System.out.println("It was not a float...:" + value.getClass().getName());
            addFieldError(fieldName, object);
        } 
        else 
        {
        	System.out.println("It was a float...:" + value.getClass().getName());
        	System.out.println("value:" + value);

        	if(value instanceof Float)
            {
                Float f = (Float) value;
            	
                if (f == null || f.equals(0)) {
                	System.out.println("adding error:" + f);
                	addFieldError(fieldName, object);
                }
            }
            else if(value instanceof Float[])
            {
            	Float[] f = (Float[]) value;
                
                if (f == null || f.length == 0) {
                	System.out.println("adding error:" + f);
                    addFieldError(fieldName, object);
                }                
            }
        }
    }
} 