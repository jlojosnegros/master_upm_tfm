package org.jlom.master_upm.tfm.springboot.recommendations.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/custom_metrics")
@Slf4j
public class CustomMetricsController {

  private long currentTimeMillis;

  @GetMapping(value = "/initial-time"
          ,produces = MediaType.APPLICATION_JSON_VALUE)
  public long initialTime() {
    return currentTimeMillis;
  }

  @EventListener
  @Async
  public void handleContextRefreshEvent(ApplicationStartedEvent applicationPreparedEvent) {
    currentTimeMillis = System.currentTimeMillis();
    log.info("ApplicationStartedEvent: " + currentTimeMillis);
  }

}
