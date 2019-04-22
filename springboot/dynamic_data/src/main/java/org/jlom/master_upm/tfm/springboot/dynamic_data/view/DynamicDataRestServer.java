package org.jlom.master_upm.tfm.springboot.dynamic_data.view;

import org.jlom.master_upm.tfm.springboot.dynamic_data.controller.UserDeviceService;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.UserDeviceCommandInterface;
import org.jlom.master_upm.tfm.springboot.dynamic_data.view.api.UserDeviceQueryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dynamic")
@Validated
public class DynamicDataRestServer implements UserDeviceQueryInterface, UserDeviceCommandInterface {

  private static final Logger LOG = LoggerFactory.getLogger(DynamicDataRestServer.class);


  private final UserDeviceService service;

  public DynamicDataRestServer(UserDeviceService service) {
    this.service = service;
  }

  @GetMapping("/mierda")
  public String mierda() {
    return "Mierda";
  }
}
