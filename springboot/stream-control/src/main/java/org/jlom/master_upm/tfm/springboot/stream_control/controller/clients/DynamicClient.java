package org.jlom.master_upm.tfm.springboot.stream_control.controller.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@FeignClient("dynamic-data")
public interface DynamicClient {
  @RequestMapping(
          value = "/dynamic-data/user-device/device/{deviceId}",
          method = RequestMethod.GET,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  InputUserDevice getUserFromDevice(@PathVariable("deviceId") long deviceId);
}
