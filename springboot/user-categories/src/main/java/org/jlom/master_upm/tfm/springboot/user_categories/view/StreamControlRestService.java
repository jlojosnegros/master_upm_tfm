package org.jlom.master_upm.tfm.springboot.user_categories.view;

import org.jlom.master_upm.tfm.springboot.user_categories.controller.StreamControlService;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.StreamControlQueryInterface;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.StreamControlCommandInterface;
import org.jlom.master_upm.tfm.springboot.user_categories.view.api.dtos.InputStreamData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequestMapping("/stream-control")
@Validated
public class StreamControlRestService implements StreamControlQueryInterface, StreamControlCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(StreamControlRestService.class);


  private final StreamControlService service;

  public StreamControlRestService(StreamControlService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<?> play(HttpServletRequest request, @Valid InputStreamData streamData) {
    return null;
  }

  @Override
  public ResponseEntity<?> stop(HttpServletRequest request, @Valid InputStreamData streamData) {
    return null;
  }

  @Override
  public ResponseEntity<?> pause(HttpServletRequest request, @Valid InputStreamData streamData) {
    return null;
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
