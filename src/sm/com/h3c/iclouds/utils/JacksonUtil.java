package com.h3c.iclouds.utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h3c.iclouds.po.Project;

public final class JacksonUtil {
	private static final String MODEL_BINDING = "{\"id\":\"name1\",\"name\":1}";
	private static final String GENERIC_BINDING = "{\"key1\":{\"name\":\"name2\",\"type\":2},\"key2\":{\"name\":\"name3\",\"type\":3}}";
	public static ObjectMapper objectMapper = null;

	/**
	 * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
	 * 
	 * @param jsonStr
	 * @param valueType
	 * @return
	 */
	public static <T> T readValue(String jsonStr, Class<T> valueType) {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			DateFormat dateFormat=new SimpleDateFormat();
			objectMapper.setDateFormat(dateFormat);
		}

		try {
			return objectMapper.readValue(jsonStr, valueType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> HashMap<String, T>  genericDataBinding(String jsonStr, Class<T> valueType) {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}

		try {
			return objectMapper.readValue(jsonStr, new TypeReference<HashMap<String, T>>() {
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 把JavaBean转换为json字符串
	 * 
	 * @param object
	 * @return
	 */
	public static String toJSon(Object object) {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}

		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	 public static void parseEpisodes(String filename) {
	        JsonFactory factory = new JsonFactory();
	        JsonParser parser = null;
	        String nameField = null;

	        try {
	            parser = factory.createJsonParser(new File(filename));

	            parser.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	            parser.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

	            JsonToken token = parser.nextToken();
	            nameField = parser.getText();
	            String desc = null;

	            while (token != JsonToken.END_OBJECT) {
	                if (nameField.equals("episodes")) {
	                    while (token != JsonToken.END_OBJECT) {
	                        if (nameField.equals("description")) {
	                            parser.nextToken();
	                            desc = parser.getText();
	                        }

	                        token = parser.nextToken();
	                        nameField = parser.getText();
	                    }
	                }

	                token = parser.nextToken();
	                nameField = parser.getText();
	            }

	            System.out.println(desc);
	        }
	        catch (JsonParseException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	@Test
	public void main() throws JsonParseException, JsonMappingException, IOException {
		Project project = new Project();
		readValue(MODEL_BINDING, project.getClass());
		Project project2 = readValue(MODEL_BINDING, project.getClass());

		// readValue(MODEL_BINDING, );
		System.out.println(project2.getId());
		System.out.println(project.getId());
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Project> modelMap = mapper.readValue(GENERIC_BINDING,
				new TypeReference<HashMap<String, Project>>() {
				});// readValue到一个范型数据中.
		Project model = modelMap.get("key2");
		System.out.println(model.getName());
		System.out.println();
	}
	
	

}