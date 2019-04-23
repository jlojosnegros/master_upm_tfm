package org.jlom.master_upm.tfm.springboot.dynamic_data.view.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface UserDeviceQueryInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @RequestMapping(
          value = "/user-device/users",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> listAllUsers(HttpServletRequest request);

  @RequestMapping(
          value = "/user-device/users/{userId}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getUser(HttpServletRequest request, @Valid @PathVariable(name = "userId") long userId);

  @RequestMapping(
          value = "/user-device/device/{deviceId}",
          method = RequestMethod.GET,
          produces = {MediaType.APPLICATION_JSON_VALUE, APPLICATION_JSON_PROBLEM_VALUE}
  )
  ResponseEntity<?> getUserForDevice(HttpServletRequest request, @Valid @PathVariable(name = "deviceId") long deviceId);

}
