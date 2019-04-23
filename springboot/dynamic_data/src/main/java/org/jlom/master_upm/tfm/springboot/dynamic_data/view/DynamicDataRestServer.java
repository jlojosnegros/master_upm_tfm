package org.jlom.master_upm.tfm.springboot.dynamic_data.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.UserDeviceService;
import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.api.dtos.UserDeviceServiceResponse;
import org.jlom.master_upm.tfm.springboot.dynamic_data.model.daos.UserDevice;
import org.jlom.master_upm.tfm.springboot.dynamic_data.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.UserDeviceCommandInterface;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.UserDeviceQueryInterface;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.dtos.InputUserDevice;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.exceptions.InvalidParamException;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.exceptions.WrapperException;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.response_handlers.CreateUserDeviceResponseHandler;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.response_handlers.UpdateUserDeviceResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.DtosTransformations.serviceToView;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.DtosTransformations.viewToService;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.JsonUtils.ListToJson;
import static org.jlom.master_upm.tfm.springboot.dynamic_data.utils.JsonUtils.ObjectToJson;


@RestController
@RequestMapping("/dynamic-data")
@Validated
public class DynamicDataRestServer implements UserDeviceQueryInterface, UserDeviceCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(DynamicDataRestServer.class);


  private final UserDeviceService service;

  public DynamicDataRestServer(UserDeviceService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<?> addDeviceToUser(HttpServletRequest request, @Valid InputUserDevice userDevice) {
    LOG.info("addDeviceToUser: userDevice=" + userDevice);


    UserDevice ud = viewToService(userDevice);

    UserDeviceServiceResponse response = service.addDevicesToUser(ud.getUserId(), ud.getDevices());

    return response.accept(new CreateUserDeviceResponseHandler(request));
  }

  @Override
  public ResponseEntity<?> removeDeviceFromUser(HttpServletRequest request, @Valid InputUserDevice userDevice) {
    LOG.info("removeDeviceFromUser: userDevice=" + userDevice);

    UserDevice ud = viewToService(userDevice);

    UserDeviceServiceResponse response = service.removeDevicesFromUser(ud.getUserId(), ud.getDevices());

    return response.accept(new UpdateUserDeviceResponseHandler(request));

  }

  @Override
  public ResponseEntity<?> listAllUsers(HttpServletRequest request) {
    LOG.info("listAllUsers");
    List<InputUserDevice> output = service.listAll()
            .stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());

    try {
      return new ResponseEntity<>(ListToJson(output), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + output, e);
    }
  }

  @Override
  public ResponseEntity<?> getUser(HttpServletRequest request, @Valid long userId) {
    LOG.info("getUser userId:" + userId);

    UserDevice user = service.getUser(userId);
    LOG.debug("user: " + user);
    InputUserDevice inputUserDevice = serviceToView(user);
    LOG.debug("output: " + inputUserDevice);
    try {
      return new ResponseEntity<>(ObjectToJson(inputUserDevice), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + inputUserDevice, e);
    }
  }

  @Override
  public ResponseEntity<?> getUserForDevice(HttpServletRequest request, @Valid long deviceId) {
    LOG.info("getUserForDevice deviceId:" + deviceId);

    Optional<Long> userForDevice = service.getUserForDevice(deviceId);
    if(userForDevice.isEmpty()) {
      throw new InvalidParamException("deviceId", "No user for deviceId:" + deviceId);
    }
    LOG.debug("userId: " + userForDevice.get());

    UserDevice user = service.getUser(userForDevice.get());
    LOG.debug("user: " + user);

    InputUserDevice inputUserDevice = serviceToView(user);
    LOG.debug("output: " + inputUserDevice);

    try {
      return new ResponseEntity<>(ObjectToJson(inputUserDevice), new HttpHeaders(), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new WrapperException("error: Unable to convertToJson obj: " + inputUserDevice, e);
    }
  }
}
