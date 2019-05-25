package org.jlom.master_upm.tfm.micronaut.stream_control.metrics;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.scheduling.annotation.Async;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Singleton
@Requires(notEnv = Environment.TEST)
@Slf4j
public class InitialEventListener implements ApplicationEventListener<ServiceStartedEvent> {

  @Getter
  private long currentTimeMillis;

  @Async
  @Override
  public void onApplicationEvent(ServiceStartedEvent event) {
    currentTimeMillis = System.currentTimeMillis();
    log.info("ServiceStartedEvent at " + currentTimeMillis + ":" + event);
  }
}
