package org.jlom.master_upm.tfm.graalvm.catalog.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.InvalidObjectException;

public class ContentStatusJsonSerializer  extends StdSerializer {

  public ContentStatusJsonSerializer() {
    super(ContentStatus.class);
  }

  public ContentStatusJsonSerializer(Class t) {
    super(t);
  }

  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {

    if(! (value instanceof ContentStatus)) {
      throw new InvalidObjectException("object: " + value + " is not a ContentStatus");
    }
    serialize((ContentStatus)value, gen, provider);
  }


  public void serialize(ContentStatus status,
                        JsonGenerator generator,
                        SerializerProvider provider)
          throws IOException {
    generator.writeStartObject();
    generator.writeFieldName("status");
    generator.writeString(status.name());
    generator.writeEndObject();
  }
}