package org.jlom.master_upm.tfm.micronaut.apigw.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class JsonUtils {

  private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);


  public static String ObjectToJson(Object obj) throws JsonProcessingException {

    ObjectMapper mapper = createConfiguredObjectMapper();
    String s = mapper.writeValueAsString(obj);
    return s;

//    Gson gson = new Gson();
//    String s = gson.toJson(obj);
//    LOG.info("jlom - obj2json: obj=" + obj);
//    LOG.info("jlom - obj2json: json=" + s);
//    return s;
  }

  private static ObjectMapper createConfiguredObjectMapper() {
    return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());
  }

  public static <T> String ListToJson(List<T> collection) throws JsonProcessingException {
    ObjectMapper mapper = createConfiguredObjectMapper();
    return mapper.writeValueAsString(collection);


//    Gson gson = new Gson();
//    String s = gson.toJson(collection);
//    LOG.info("jlom - col2json: col=" + collection);
//    LOG.info("jlom - col2json: json=" + s);
//    return s;
  }

  public static <T> T jsonToObject(String json, Class<T> clazz) throws IOException {
    ObjectMapper mapper = createConfiguredObjectMapper();
    return mapper.readValue(json, clazz);

//    Gson gson = new Gson();
//    Type type = new TypeToken<T>() {}.getType();
//    T o = gson.fromJson(json, type);
//    LOG.info("jlom - json2obj: json=" + json);
//    LOG.info("jlom - json2obj: obj=" + o);
//    return o;
  }

  public static <T> List<T> jsonToList(String json, Class<T> clazz) throws IOException {
    ObjectMapper mapper = createConfiguredObjectMapper();

    return mapper.readValue(json,mapper.getTypeFactory()
                                       .constructCollectionType(List.class, clazz));

    // Converts JSON string into a collection of Student object.
//    Gson gson = new Gson();
//    Type type = new TypeToken<List<T>>() {}.getType();
//    List<T> o = gson.fromJson(json, type);
//    LOG.info("jlom - json2col: json=" + json);
//    LOG.info("jlom - json2col: col=" + o);
//    return o;
  }
}
