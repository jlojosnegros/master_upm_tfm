package org.jlom.master_upm.tfm.springboot.dynamic_data.view.api;

import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.dtos.InputUserDevice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface UserDeviceCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/user-device/add-devices",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> addDeviceToUser(HttpServletRequest request, @Valid @RequestBody InputUserDevice userDevice);

  @RequestMapping(
          value = "/user-device/remove-devices",
          method = RequestMethod.POST,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> removeDeviceFromUser(HttpServletRequest request, @Valid @RequestBody InputUserDevice userDevice);

}
