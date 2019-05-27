package org.jlom.master_upm.tfm.graalvm.dynamic_data.view.api;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import org.jlom.master_upm.tfm.graalvm.dynamic_data.view.api.dtos.InputUserDevice;

import javax.validation.Valid;

public interface UserDeviceCommandInterface {

  String APPLICATION_JSON_PROBLEM_VALUE = "application/problem+json";

  @Post("/user-device/add-devices")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> addDeviceToUser(HttpRequest request, @Valid @Body InputUserDevice userDevice);

  @Post("/user-device/remove-devices")
  @Produces({MediaType.APPLICATION_JSON, APPLICATION_JSON_PROBLEM_VALUE})
  HttpResponse<?> removeDeviceFromUser(HttpRequest request, @Valid @Body InputUserDevice userDevice);

}
