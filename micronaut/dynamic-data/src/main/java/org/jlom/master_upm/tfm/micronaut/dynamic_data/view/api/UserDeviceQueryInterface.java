package org.jlom.master_upm.tfm.micronaut.dynamic_data.view.api;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;

import javax.validation.Valid;

public interface UserDeviceQueryInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Get("/user-device/users")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> listAllUsers(HttpRequest request);

  @Get("/user-device/users/{userId}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getUser(HttpRequest request, @Valid @PathVariable(name = "userId") long userId);

  @Get("/user-device/device/{deviceId}")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> getUserForDevice(HttpRequest request, @Valid @PathVariable(name = "deviceId") long deviceId);

}
