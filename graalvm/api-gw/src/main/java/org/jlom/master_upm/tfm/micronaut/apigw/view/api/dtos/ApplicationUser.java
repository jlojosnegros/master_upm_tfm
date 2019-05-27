package org.jlom.master_upm.tfm.micronaut.apigw.view.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class ApplicationUser implements Serializable {

  private final static long serialVersionUID = 1L;

  @JsonProperty(value = "username",required = true)
  private  String username;

  @JsonProperty(value = "password",required = true)
  private  String password;
}