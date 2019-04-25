package org.jlom.master_upm.tfm.springboot.user_categories.view.api;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputStreamData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface StreamControlCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/play",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> play(HttpServletRequest request, @Valid @RequestBody InputStreamData streamData);

  @RequestMapping(
          value = "/stop",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> stop(HttpServletRequest request, @Valid @RequestBody InputStreamData streamData);

  @RequestMapping(
          value = "/pause",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> pause(HttpServletRequest request, @Valid @RequestBody InputStreamData streamData);

}
