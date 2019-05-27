package org.jlom.master_upm.tfm.graalvm.stream_control.view;





import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.validation.Validated;
import org.jlom.master_upm.tfm.graalvm.stream_control.controller.StreamControlService;
import org.jlom.master_upm.tfm.graalvm.stream_control.utils.JsonUtils;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.StreamControlCommandInterface;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.StreamControlQueryInterface;
import org.jlom.master_upm.tfm.graalvm.stream_control.view.api.dtos.InputStreamData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;


@Controller("/stream-control")
@Validated
public class StreamControlRestService implements StreamControlQueryInterface, StreamControlCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlRestService.class);


  private final StreamControlService service;

  public StreamControlRestService(StreamControlService service) {
    this.service = service;
  }

  @Override
  public HttpResponse<?> play(HttpRequest request, @Valid InputStreamData streamData) {
    return null;
  }

  @Override
  public HttpResponse<?> stop(HttpRequest request, @Valid InputStreamData streamData) {
    return null;
  }

  @Override
  public HttpResponse<?> pause(HttpRequest request, @Valid InputStreamData streamData) {
    return null;
  }


  @Get("/startdate")
  @Produces(MediaType.APPLICATION_JSON)
  public HttpResponse<?> startDate() throws JsonProcessingException {
    return HttpResponse.ok(JsonUtils.ObjectToJson(StartTime.getInstance().getStartTime()));
  }
  // @Override
  // public ResponseEntity<?> addDeviceToUser(HttpServletRequest request, @Valid InputStreamData userDevice) {
  //   LOG.info("addDeviceToUser: userDevice=" + userDevice);


  //   StreamControlData ud = viewToService(userDevice);

  //   StreamControlServiceResponse response = service.addDevicesToUser(ud.getUserId(), ud.getDevices());

  //   return response.accept(new CreateStreamControlResponseHandler(request));
  // }

 

  // @Override
  // public ResponseEntity<?> listAllUsers(HttpServletRequest request) {
  //   LOG.info("listAllUsers");
  //   List<InputStreamData> output = service.listAll()
  //           .stream()
  //           .map(DtosTransformations::serviceToView)
  //           .collect(Collectors.toList());

  //   try {
  //     return new ResponseEntity<>(ListToJson(output), new HttpHeaders(), HttpStatus.OK);
  //   } catch (JsonProcessingException e) {
  //     throw new WrapperException("error: Unable to convertToJson obj: " + output, e);
  //   }
  // }

  
}
