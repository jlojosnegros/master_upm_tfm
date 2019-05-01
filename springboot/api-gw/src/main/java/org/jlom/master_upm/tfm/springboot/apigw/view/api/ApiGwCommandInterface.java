package org.jlom.master_upm.tfm.springboot.apigw.view.api;

import org.jlom.master_upm.tfm.springboot.apigw.view.api.dtos.InputCatalogContent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface ApiGwCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/stream/play",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> play(HttpServletRequest request, @Valid @RequestBody InputCatalogContent content);
}
