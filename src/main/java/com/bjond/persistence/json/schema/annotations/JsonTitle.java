package com.bjond.persistence.json.schema.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This simple annotation allows us to set the title of a given field. The
 * title will be serialized into the JSON schema as a property, and is the label on
 * the field in the generated UI.
 * 
 * @author Benjamin Flynn
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonTitle {
	public String title() default "";
}
