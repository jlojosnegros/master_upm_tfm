package org.jlom.master_upm.tfm.micronaut.recommendations.controller.clients;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

// OJO Esta clase es la misma que esta en el dynamic_data, pero no me deja importarla,
// no tengo ni puta idea de porque no me deja y no tengo tiempo para las gilipolleces
// del puto java, maven y la madre que los pario a todos.

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputUserDevice implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "user-id",required = true)
  private  String userId;

  @JsonProperty(value = "device-ids",required = true)
  private Set<String> devices;
}

