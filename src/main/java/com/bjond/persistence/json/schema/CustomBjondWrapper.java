package com.bjond.persistence.json.schema;

import java.lang.reflect.Field;

import com.bjond.persistence.json.schema.annotations.JsonClientCache;
import com.bjond.persistence.json.schema.annotations.JsonTitle;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.*;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;

import lombok.Getter;

/**
 * This class allows us to add some extra properties to JSON Schema through annotations.
 * 
 * Initially this was an example how to add a title field, but I edited it to handle
 * all field types and to use a custom annotation to set the title value. We can
 * use this to add other custom fields as needed as well.
 * 
 * @author Benjamin Flynn
 */
public class CustomBjondWrapper extends SchemaFactoryWrapper
{
	@Getter
	private Class<?> originalClass;
	
    private static class CustomBjondWrapperFactory extends WrapperFactory {
    	
    	@Getter
    	private Class<?> originalClass;
    	
    	public CustomBjondWrapperFactory() {
    		super();
    	}
    	
    	public CustomBjondWrapperFactory(Class<?> clazz) {
    		super();
    		originalClass = clazz;
    	}
    	
	    @Override
	    public SchemaFactoryWrapper getWrapper(SerializerProvider p) {
	        SchemaFactoryWrapper wrapper = new CustomBjondWrapper(originalClass);
	        if (p != null) {
	            wrapper.setProvider(p);
	        }
	        return wrapper;
	    };

	    @Override
	    public SchemaFactoryWrapper getWrapper(SerializerProvider p, VisitorContext rvc) {
            SchemaFactoryWrapper wrapper = new CustomBjondWrapper(originalClass);
            if (p != null) {
                wrapper.setProvider(p);
            }
            wrapper.setVisitorContext(rvc);
            return wrapper;
        }
    };

	public CustomBjondWrapper() {
		super(new CustomBjondWrapperFactory());
	}
	
	public CustomBjondWrapper(Class<?> clazz) {
		super(new CustomBjondWrapperFactory(clazz));
		originalClass = clazz;
	}

	/**
	 * Simple override- when it's an Object, you do have access to the annotations, so we can 
	 * add the title to the root element.
	 */
    @Override
    public JsonObjectFormatVisitor expectObjectFormat(JavaType convertedType) {
		ObjectVisitor visitor = ((ObjectVisitor)super.expectObjectFormat(convertedType));
		JsonTitle titleAnnotation = convertedType.getRawClass().getAnnotation(JsonTitle.class);
		if(titleAnnotation != null) {
			String title = titleAnnotation.title();
			addTitle(visitor.getSchema(), title);
		}
		return visitor;
    }

    /**
     * Adds writes the type as the title of the schema.
     * 
     * @param schema The schema who's title to set.
     * @param title The title of the object represented by the schema.
     */
   private void addTitle(JsonSchema schema, String title)
   {
       if (!schema.isSimpleTypeSchema()) {
           throw new RuntimeException("given non simple type schema: " + schema.getType());
       }
       schema.asSimpleTypeSchema().setTitle(title);
   }
   
   /**
    * Unfortunately the methods that handle serializing each field don't send the field object or the 
    * actual field content, so I need to iterate the properties in the final accumulation. I have the
    * original class definition passed as a member through the wrapper chain so the fields can be found
    * by name.
    */
   @Override
   public JsonSchema finalSchema() {
	   JsonSchema schema = super.finalSchema();
	   if(schema.isObjectSchema()) {
		   ObjectSchema oSchema = (ObjectSchema) schema;
		   for(String key : oSchema.getProperties().keySet()) {
			   for(Field field : this.originalClass.getDeclaredFields()) {
				   if(field.getName().equals(key)) {
					   JsonTitle title = field.getAnnotation(JsonTitle.class);
					   if(title != null) {
						   addTitle(oSchema.getProperties().get(key), title.title());
					   }
					   JsonClientCache cache = field.getAnnotation(JsonClientCache.class);
					   if(cache != null) {
						   // TODO: Create a subclass of the schema so we can add a field here.
					   }
				   }
			   }
		   }
	   }
	   return schema;
   }
}