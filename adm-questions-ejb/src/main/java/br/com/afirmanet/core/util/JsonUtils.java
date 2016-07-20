package br.com.afirmanet.core.util;

import java.util.ArrayList;
import java.util.List;

import br.com.afirmanet.core.exception.ApplicationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public final class JsonUtils {

	private JsonUtils() {

	}

	public static String toString(Object value) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new ApplicationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T toObject(String content, T t) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return (T) mapper.readValue(content, t.getClass());
		} catch (Throwable e) {
			throw new ApplicationException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> toListObject(String content, T typeDef) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeFactory typeFactory = TypeFactory.defaultInstance();
			return (List<T>) mapper.readValue(content, typeFactory.constructCollectionType(ArrayList.class, typeDef.getClass()));
		} catch (Throwable e) {
			throw new ApplicationException(e);
		}
	}

}