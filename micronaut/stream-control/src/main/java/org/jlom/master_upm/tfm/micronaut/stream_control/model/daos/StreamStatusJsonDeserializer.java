package org.jlom.master_upm.tfm.micronaut.stream_control.model.daos;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class StreamStatusJsonDeserializer extends StdDeserializer<StreamStatus> {

  public StreamStatusJsonDeserializer() {
    this(null);
  }

  public StreamStatusJsonDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public StreamStatus deserialize(JsonParser jp, DeserializationContext ctxt)
          throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    String status = node.get("status").asText();
    return StreamStatus.valueOf(status);
  }
}
