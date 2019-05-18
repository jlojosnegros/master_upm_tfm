package org.jlom.master_upm.tfm.micronaut.stream_control.model;


import io.micronaut.test.annotation.MicronautTest;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.api.IStreamControlRepository;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamControlData;
import org.jlom.master_upm.tfm.micronaut.stream_control.model.daos.StreamStatus;
import org.jlom.master_upm.tfm.micronaut.stream_control.utils.EmbeddedRedisServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class ModelTest {

//  @Rule
//  public GenericContainer rabbitmq = new GenericContainer<>("library/rabbitmq:3.7")
//          .withExposedPorts(5672)
//          .waitingFor(new LogMessageWaitStrategy().withRegEx("(?s).*Server startup complete.*"));

  @Inject
  private IStreamControlRepository streamControlRepository;

  private static final Logger LOG = LoggerFactory.getLogger(ModelTest.class);

  @Inject
  EmbeddedRedisServer embeddedRedisServer;

  @BeforeEach
  public void setup() {
    embeddedRedisServer.start();
  }
  @AfterEach
  public void tearDown() {
    embeddedRedisServer.stop();
  }


  @Test
  public void given_NoData_when_findRunning_then_ShouldNotFindAnything() {

    final long userId = 1;
    final long deviceId =1;

    StreamControlData streamingRunning = streamControlRepository.findStreamingRunning(userId, deviceId);

    assertThat(streamingRunning).isNull();

  }

  @Test
  public void given_OneData_when_findRunning_then_ShouldNotFindAnything() {

    StreamControlData OneRunning = StreamControlData.builder()
            .userId(1)
            .deviceId(1)
            .streamId(1)
            .status(StreamStatus.RUNNING)
            .build();
    streamControlRepository.save(OneRunning);

    StreamControlData streamingRunning = streamControlRepository.findStreamingRunning(OneRunning.getUserId()
            ,OneRunning.getDeviceId());

    assertThat(streamingRunning).isNotNull();
    assertThat(streamingRunning.getStatus()).isEqualTo(StreamStatus.RUNNING);
    assertThat(streamingRunning).isEqualTo(OneRunning);

  }

  @Test
  public void given_SomeData_when_findRunning_then_ShouldNotFindAnything() {

    StreamControlData OneRunning = StreamControlData.builder()
            .userId(1)
            .deviceId(1)
            .streamId(1)
            .status(StreamStatus.RUNNING)
            .build();
    streamControlRepository.save(OneRunning);

    StreamControlData streamingRunning = streamControlRepository.findStreamingRunning(OneRunning.getUserId()
            ,OneRunning.getDeviceId());

    assertThat(streamingRunning).isEqualTo(OneRunning);

    OneRunning.setStatus(StreamStatus.DONE);
    streamControlRepository.save(OneRunning);

    streamingRunning = streamControlRepository.findStreamingRunning(OneRunning.getUserId()
            ,OneRunning.getDeviceId());

    assertThat(streamingRunning).isNull();

  }
}
