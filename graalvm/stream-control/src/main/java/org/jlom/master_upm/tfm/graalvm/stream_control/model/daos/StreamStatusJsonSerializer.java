package org.jlom.master_upm.tfm.graalvm.stream_control.model.daos;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.InvalidObjectException;

public class StreamStatusJsonSerializer extends StdSerializer {

  public StreamStatusJsonSerializer() {
    super(StreamStatus.class);
  }

  public StreamStatusJsonSerializer(Class t) {
    super(t);
  }

  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {

    if(! (value instanceof StreamStatus)) {
      throw new InvalidObjectException("object: " + value + " is not a ContentStatus");
    }
    serialize((StreamStatus)value, gen, provider);
  }


  public void serialize(StreamStatus status,
                        JsonGenerator generator,
                        SerializerProvider provider)
          throws IOException {
    generator.writeStartObject();
    generator.writeFieldName("status");
    generator.writeString(status.name());
    generator.writeEndObject();
  }
}