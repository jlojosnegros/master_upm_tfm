package org.jlom.master_upm.tfm.springboot.dynamic_data.view;

import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.UserDeviceService;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.UserDeviceCommandInterface;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.UserDeviceQueryInterface;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.dtos.InputUserDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    return null;
  }

  @Override
  public ResponseEntity<?> removeDeviceFromUser(HttpServletRequest request, @Valid InputUserDevice userDevice) {
    return null;
  }

  @Override
  public ResponseEntity<?> listAllUsers(HttpServletRequest request) {
    return null;
  }

  @Override
  public ResponseEntity<?> getDevicesByUser(HttpServletRequest request, @Valid long userId) {
    return null;
  }

  @Override
  public ResponseEntity<?> getUserForDevice(HttpServletRequest request, @Valid long deviceId) {
    return null;
  }
}
