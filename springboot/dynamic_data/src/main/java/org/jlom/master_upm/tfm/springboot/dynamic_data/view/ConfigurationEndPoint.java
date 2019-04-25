package org.jlom.master_upm.tfm.springboot.dynamic_data.view;

import org.jlom.master_upm.tfm.springboot.dynamic_data.config.PropertiesReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dynamic-data/configuration")
public class ConfigurationEndPoint {

  @Autowired
  private PropertiesReading propertiesReading;

  @RequestMapping(
          value = "prop",
          method = RequestMethod.GET
  )
  public String getProperty() {
    return "leyendo: " + propertiesReading.getProp();
  }

  @RequestMapping(
          value = "prop_test",
          method = RequestMethod.GET
  )
  public String getPropertyTest() {
    return "leyendo-test: " + propertiesReading.getPropTest();
  }
}
