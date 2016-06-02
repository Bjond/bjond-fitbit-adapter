package com.bjond.persistence.json.schema;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;

import lombok.Getter;
import lombok.Setter;

public class BjondMapper extends ObjectMapper {
	
	private static final long serialVersionUID = -8043538434388139100L;
	
	@Getter
	@Setter
	private Field[] fields;
	
	@Override
	public JavaType constructType(Type t) {
		return super.constructType(t);
	}
	
	@Override
	public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
		fields = type.getDeclaredFields();
		super.acceptJsonFormatVisitor(type, visitor);
	}
}
