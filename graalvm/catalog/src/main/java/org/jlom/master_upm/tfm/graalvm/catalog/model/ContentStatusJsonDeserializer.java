package org.jlom.master_upm.tfm.graalvm.catalog.model;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ContentStatusJsonDeserializer extends StdDeserializer<ContentStatus> {

  public ContentStatusJsonDeserializer () {
    this(null);
  }

  public ContentStatusJsonDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public ContentStatus deserialize(JsonParser jp, DeserializationContext ctxt)
          throws IOException {
    JsonNode node = jp.getCodec().readTree(jp);
    String status = node.get("status").asText();
    return ContentStatus.valueOf(status);
  }
}
